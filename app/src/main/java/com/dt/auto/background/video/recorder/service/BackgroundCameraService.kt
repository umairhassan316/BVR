package com.dt.auto.background.video.recorder.service


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.google.android.material.badge.BadgeDrawable
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.isPurchase
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.helpers.extensions.getAspectRatio
import com.dt.auto.background.video.recorder.helpers.extensions.getAspectRatioString
import com.dt.auto.background.video.recorder.helpers.extensions.getNameString
import com.dt.auto.background.video.recorder.helpers.FormattedTimeHelper.getFormattedStopWatchTime
import com.dt.auto.background.video.recorder.helpers.VersionCheckHelper.isOreoPlus
import com.dt.auto.background.video.recorder.app_ui.activity.quality_selection.CAMERA_CHECK_FRONT_OR_BACK
import com.dt.auto.background.video.recorder.app_ui.activity.quality_selection.DEFAULT_QUALITY_IDX
import com.dt.auto.background.video.recorder.helpers.utils.Constants.ACTION_START_SERVICE
import com.dt.auto.background.video.recorder.helpers.utils.Constants.ACTION_STOP_SERVICE
import com.dt.auto.background.video.recorder.helpers.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.dt.auto.background.video.recorder.helpers.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.dt.auto.background.video.recorder.helpers.utils.Constants.NOTIFICATION_ID
import com.dt.auto.background.video.recorder.helpers.utils.Constants.TIMER_UPDATE_INTERVAL
import com.dt.auto.background.video.recorder.helpers.utils.invisible
import com.dt.auto.background.video.recorder.helpers.utils.setOnClickListenerCoolDown
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class NoScreenVideoRecorderService : LifecycleService() {

    private lateinit var recordingState: VideoRecordEvent

    // Camera UI  states and inputs
    enum class UiState {
        IDLE,       // Not recording, all UI controls are active.
        RECORDING,  // Camera is recording, only display Pause/Resume & Stop button.
        FINALIZED,  // Recording just completes, disable all RECORDING UI controls.
        RECOVERY    // For future use.
    }

    companion object {

        private val captureLiveStatus = MutableLiveData<String>()

        private lateinit var videoCapture: VideoCapture<Recorder>
        private var currentRecording: Recording? = null


        private var cameraIndex = CAMERA_CHECK_FRONT_OR_BACK
        private var qualityIndex = DEFAULT_QUALITY_IDX
        private var audioEnabled = true

        private var enumerationDeferred: Deferred<Unit>? = null

        private val cameraCapabilities = mutableListOf<CameraCapability>()


        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()

        var mWindowManager: WindowManager? = null
        var mFloatingView: View? = null

        var mTextDuration: TextView? = null

        private const val TAG = "ffnet"
        var countDownTimer: CountDownTimer? = null


        private var mCurrentFile: File? = null


    }

    var inflate: View? = null
    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(this) }

    private val timeRunInSeconds = MutableLiveData<Long>()

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder


    private var mContext: Context? = null


    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        curNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        CAMERA_CHECK_FRONT_OR_BACK = settings.isBackCameraInt
        isTracking.observe(this) {
            updateNotificationRecordingState(it)
        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    startForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    try {
                        postInitialValues()
                        stopForeground(true)
                        stopSelf()
                        stopService()
                    } catch (e: Exception) {
                    }

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopService() {

        isTimerEnabled = false
        try {
            if (mWindowManager != null) {
                mWindowManager?.removeView(mFloatingView)
            }
            if (countDownTimer != null) {
                countDownTimer?.cancel()
            }
        } catch (e: Exception) {
        }


    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startRecordingTimer() {
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lapTime
                timeRunInMillis.postValue(timeRun + lapTime)
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }


    private fun postInitialValues() {
        isTracking.postValue(false)
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            postInitialValues()
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            stopForeground(true)
            stopSelf()
            stopService()

        } catch (e: Exception) {
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }


    private fun updateNotificationRecordingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Stop" else "Start"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, NoScreenVideoRecorderService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, flags)
        } else {
            val resumeIntent = Intent(this, NoScreenVideoRecorderService::class.java).apply {
                action = ACTION_START_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, flags)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        curNotificationBuilder = baseNotificationBuilder.addAction(
            R.drawable.ic_stop,
            notificationActionText,
            pendingIntent
        )
        if (!isTracking) {
            notificationManager.cancelAll()
        }

        notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
    }


    private val flags = when{
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            PendingIntent.FLAG_IMMUTABLE
        else -> {PendingIntent.FLAG_MUTABLE}
    }


    private fun startForegroundService() {
        startRecordingTimer()
        isTracking.postValue(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (isOreoPlus()) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSeconds.observe(this) {
            val notification =
                curNotificationBuilder.setContentText(getFormattedStopWatchTime(it * 1000L))
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        }


        try {
            showView()
        } catch (e: Exception) {
            val errorMsg = "${e}"
            Log.e(TAG, errorMsg)
        }

    }


    private fun showView() {

        mWindowManager = getSystemService("window") as WindowManager

        if (!settings.hasPreview) {

            val layoutParams = WindowManager.LayoutParams(
                2,
                2,
                if (Build.VERSION.SDK_INT >= 26) 2038 else 2002,
                8,
                -3
            )
            layoutParams.gravity = BadgeDrawable.TOP_START
            layoutParams.x = 0
            layoutParams.y = 0


            if (settings.previewSize?.equals(application.getString(R.string.medium)) == true) {
                inflate = LayoutInflater.from(this)
                    .inflate(R.layout.medium_preview_layout, null as ViewGroup?)
            } else if (settings.previewSize?.equals(application.getString(R.string.small)) == true) {
                inflate =
                    LayoutInflater.from(this)
                        .inflate(R.layout.small_preview_layout, null as ViewGroup?)
            } else if (settings.previewSize?.equals(application.getString(R.string.large)) == true) {
                inflate =
                    LayoutInflater.from(this)
                        .inflate(R.layout.large_preview_layout, null as ViewGroup?)
            }


            inflate?.alpha = 0f
            mFloatingView = inflate
            mWindowManager?.addView(inflate, layoutParams)


            val defaultDisplay: Display? = mWindowManager?.defaultDisplay
            val point = Point()
            defaultDisplay?.getSize(point)
            var i2 = point.x
            var i3 = point.y
            val e3: Int = 0
            when (e3) {
                0 -> {
                    i2 /= 2
                    i3 /= 2
                }
                1 -> {
                    i2 = i2 * 3 / 5
                    i3 = i3 * 3 / 5
                }
                2 -> {
                    i2 = i2 * 4 / 5
                    i3 = i3 * 4 / 5
                }
            }
            (mFloatingView?.findViewById(R.id.container) as RelativeLayout).layoutParams =
                RelativeLayout.LayoutParams(i2, i3)

            mTextDuration = mFloatingView?.findViewById(R.id.duration_tv)
            timeRunInSeconds.observe(this) {
                mTextDuration?.text = (getFormattedStopWatchTime(it * 1000L))
            }


            mFloatingView?.findViewById<View>(R.id.btn_stop)
                ?.setOnClickListenerCoolDown {
                    try {
                        val notificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancelAll()
                        stopSelf()
                        stopService()
                        val recording = currentRecording
                        if (recording != null) {
                            recording.stop()
                            currentRecording = null
                        }
                    } catch (e: Exception) {
                    }
                }
            mFloatingView?.findViewById<View>(R.id.container)
                ?.setOnTouchListener(setOnTouchListener(layoutParams))


        } else {
            val layoutParams = WindowManager.LayoutParams(
                -2,
                -2,
                if (Build.VERSION.SDK_INT >= 26) 2038 else 2002,
                8,
                -3
            )
            layoutParams.gravity = BadgeDrawable.TOP_START
            layoutParams.x = 0
            layoutParams.y = 0



            if (settings.previewSize?.equals(application.getString(R.string.medium)) == true) {
                inflate = LayoutInflater.from(this)
                    .inflate(R.layout.medium_preview_layout, null as ViewGroup?)
            } else if (settings.previewSize?.equals(application.getString(R.string.small)) == true) {
                inflate =
                    LayoutInflater.from(this)
                        .inflate(R.layout.small_preview_layout, null as ViewGroup?)
            } else if (settings.previewSize?.equals(application.getString(R.string.large)) == true) {
                inflate =
                    LayoutInflater.from(this)
                        .inflate(R.layout.large_preview_layout, null as ViewGroup?)
            }
            mFloatingView = inflate
            mWindowManager?.addView(inflate, layoutParams)


            val defaultDisplay: Display? = mWindowManager?.defaultDisplay
            val point = Point()
            defaultDisplay?.getSize(point)
            var i2 = point.x
            var i3 = point.y
            val e3: Int = 0
            when (e3) {
                0 -> {
                    i2 /= 2
                    i3 /= 2
                }
                1 -> {
                    i2 = i2 * 3 / 5
                    i3 = i3 * 3 / 5
                }
                2 -> {
                    i2 = i2 * 4 / 5
                    i3 = i3 * 4 / 5
                }
            }
            (mFloatingView?.findViewById(R.id.container) as RelativeLayout).layoutParams =
                RelativeLayout.LayoutParams(i2, i3)

            mTextDuration = mFloatingView?.findViewById(R.id.duration_tv)
            timeRunInSeconds.observe(this) {
                mTextDuration?.text = (getFormattedStopWatchTime(it * 1000L))
            }


            mFloatingView?.findViewById<View>(R.id.btn_stop)
                ?.setOnClickListenerCoolDown {
                    try {
                        val notificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancelAll()
                        stopSelf()
                        stopService()
                        val recording = currentRecording
                        if (recording != null) {
                            recording.stop()
                            currentRecording = null
                        }
                    } catch (e: Exception) {
                    }
                }
            mFloatingView?.findViewById<View>(R.id.container)
                ?.setOnTouchListener(setOnTouchListener(layoutParams))


        }


        initVideoRecording()

    }


    // main cameraX capture functions
    /**
     *   Always bind preview + video capture use case combinations in this sample
     *   (VideoCapture can work on its own). The function should always execute on
     *   the main thread.
     */
    private suspend fun bindCaptureUsecase() {
        val cameraProvider = ProcessCameraProvider.getInstance(this).await()

        /*
        * TODO: bindCaptureUsecase :: Quality selector
        * */
        val cameraSelector = getCameraSelector(settings.isBackCameraInt)

        // create the user required QualitySelector (video resolution): we know this is
        // supported, a valid qualitySelector will be created.

        val quality = try {
            cameraCapabilities[settings.isBackCameraInt].qualities[settings.videoQualitySelected]
        } catch (e: Exception) {
            cameraCapabilities[settings.isBackCameraInt].qualities[0]
        }
        val qualitySelector = QualitySelector.from(quality)
        if (!settings.hasPreview) {
            mFloatingView?.findViewById<ConstraintLayout>(R.id.root_container)?.invisible()
            mFloatingView?.findViewById<PreviewView>(R.id.previewView)?.invisible()
        }
        mFloatingView?.findViewById<PreviewView>(R.id.previewView)
            ?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                val orientation = resources.configuration.orientation
                dimensionRatio = quality.getAspectRatioString(
                    quality,
                    (orientation == Configuration.ORIENTATION_PORTRAIT)
                )
            }

        val preview = Preview.Builder()
            .setTargetAspectRatio(quality.getAspectRatio(quality))
            .build().apply {
                setSurfaceProvider(mFloatingView?.findViewById<PreviewView>(R.id.previewView)?.surfaceProvider)
            }

        // build a recorder, which can:
        //   - record video/audio to MediaStore(only shown here), File, ParcelFileDescriptor
        //   - be used create recording(s) (the recording performs recording)
        val recorder = Recorder.Builder()
            .setQualitySelector(qualitySelector)
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                videoCapture,
                preview
            )
        } catch (exc: Exception) {
            // we are on main thread, let's reset the controls on the UI.
            Log.e(TAG, "Use case binding failed", exc)
            resetUIandState("bindToLifecycle failed: $exc")
        }
        enableUI(true)
        if (!this@NoScreenVideoRecorderService::recordingState.isInitialized ||
            recordingState is VideoRecordEvent.Finalize
        ) {

            enableUI(false)  // Our eventListener will turn on the Recording UI.
            startRecording()
        } else {
            when (recordingState) {
                is VideoRecordEvent.Start -> {
                    currentRecording?.pause()
                    mFloatingView?.findViewById<ImageButton>(R.id.btn_stop)?.visibility =
                        View.VISIBLE
                }
                is VideoRecordEvent.Pause -> currentRecording?.resume()
                is VideoRecordEvent.Resume -> currentRecording?.pause()
                else -> throw IllegalStateException("recordingState in unknown state")
            }
        }
    }


    /**
     * ResetUI (restart):
     *    in case binding failed, let's give it another change for re-try. In future cases
     *    we might fail and user get notified on the status
     */
    private fun resetUIandState(reason: String) {
        enableUI(true)
        showUI(UiState.IDLE, reason)

        /*
        * TODO: resetUIandState : Replace the camera indexs state with constant I've define
        * */
        cameraIndex = settings.isBackCameraInt //CAMERA_CHECK_FRONT_OR_BACK
        qualityIndex = settings.videoQualitySelected // DEFAULT_QUALITY_IDX
        audioEnabled = true
        initializeQualitySectionsUI()
    }


    /**
     *  initializeQualitySectionsUI():
     *    Populate a RecyclerView to display camera capabilities:
     *       - one front facing
     *       - one back facing
     *    User selection is saved to qualityIndex, will be used
     *    in the bindCaptureUsecase().
     */
    private fun initializeQualitySectionsUI() {
        qualityIndex = settings.videoQualitySelected
    }


    /**
     * Enable/disable UI:
     *    User could select the capture parameters when recording is not in session
     *    Once recording is started, need to disable able UI to avoid conflict.
     */
    private fun enableUI(enable: Boolean) {
    }

    /**
     * initialize UI for recording:
     *  - at recording: hide audio, qualitySelection,change camera UI; enable stop button
     *  - otherwise: show all except the stop button
     */
    private fun showUI(state: UiState, status: String = "idle") {
        mFloatingView?.let {
            when (state) {
                UiState.IDLE -> {

                }
                UiState.RECORDING -> {


                }
                UiState.FINALIZED -> {

                }
                else -> {
                    val errorMsg = "Error: showUI($state) is not supported"
                    Log.e(TAG, errorMsg)
                    return
                }
            }
            it.findViewById<TextView>(R.id.capture_status)?.text = status

        }
    }


    /**
     * Retrieve the asked camera's type(lens facing type). In this sample, only 2 types:
     *   idx is even number:  CameraSelector.LENS_FACING_BACK
     *          odd number:   CameraSelector.LENS_FACING_FRONT
     */
    private fun getCameraSelector(idx: Int): CameraSelector {
        if (cameraCapabilities.size == 0) {
            sendCommandToService(ACTION_STOP_SERVICE)
        }
        return (cameraCapabilities[idx % cameraCapabilities.size].camSelector)
    }

    data class CameraCapability(val camSelector: CameraSelector, val qualities: List<Quality>)

    /**
     * Query and cache this platform's camera capabilities, run only once.
     */
    init {
        enumerationDeferred = lifecycleScope.async {
            whenCreated {
                val provider =
                    ProcessCameraProvider.getInstance(this@NoScreenVideoRecorderService).await()

                provider.unbindAll()
                for (camSelector in arrayOf(
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    CameraSelector.DEFAULT_FRONT_CAMERA
                )) {
                    try {
                        // just get the camera.cameraInfo to query capabilities
                        // we are not binding anything here.
                        if (provider.hasCamera(camSelector)) {
                            val camera = provider.bindToLifecycle(
                                this@NoScreenVideoRecorderService,
                                camSelector
                            )
                            QualitySelector
                                .getSupportedQualities(camera.cameraInfo)
                                .filter { quality ->
                                    listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD)
                                        .contains(quality)
                                }.also {
                                    cameraCapabilities.add(
                                        CameraCapability(
                                            camSelector,
                                            it
                                        )
                                    )
                                }
                        }
                    } catch (exc: java.lang.Exception) {
                        Log.e(TAG, "Camera Face $camSelector is not supported")
                    }
                }
            }
        }


    }


    private fun sendCommandToService(action: String) =
//        Intent(applicationContext, NoScreenVideoRecorderService::class.java).also {
        Intent(applicationContext, NoScreenVideoRecorderService::class.java).also {
            it.action = action
            startService(it)
        }


    internal class setOnTouchListener(
        val wm: WindowManager.LayoutParams
    ) : View.OnTouchListener {
        private var f4008f = 0

        private var f4009g = 0

        private var f4010h = 0f

        private var f4011i = 0f

        override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
            val action = motionEvent.action
            return if (action == 0) {
                val layoutParams = wm
                f4008f = layoutParams.x
                f4009g = layoutParams.y
                f4010h = motionEvent.rawX
                f4011i = motionEvent.rawY
                true
            } else if (action != 2) {
                false
            } else {
                wm.x = f4008f + (motionEvent.rawX - f4010h).toInt()
                wm.y = f4009g + (motionEvent.rawY - f4011i).toInt()
                mWindowManager?.updateViewLayout(
                    mFloatingView,
                    wm
                )
                true
            }
        }
    }


    /**
     * Initialize UI. Preview and Capture actions are configured in this function.
     * Note that preview and capture are both initialized either by UI or CameraX callbacks
     * (except the very 1st time upon entering to this fragment in onCreateView()
     */
    @SuppressLint("ClickableViewAccessibility", "MissingPermission")
    private fun initializeUI() {


//        if (!settings.hasPreview) {
//            mFloatingView?.invisible()
//            mFloatingView?.findViewById<ConstraintLayout>(R.id.root_container)?.invisible()
//            mFloatingView?.findViewById<PreviewView>(R.id.previewView)?.invisible()
//        }
        mFloatingView?.findViewById<TextView>(R.id.capture_status)?.invisible()
        captureLiveStatus.observe(this) {
            mFloatingView?.findViewById<TextView>(R.id.capture_status)?.apply {
                post { text = it }
            }
        }
        captureLiveStatus.value = getString(R.string.Idle)
    }


    /**
     * Kick start the video recording
     *   - config Recorder to capture to MediaStoreOutput
     *   - register RecordEvent Listener
     *   - apply audio request from user
     *   - start recording!
     * After this function, user could start/pause/resume/stop recording and application listens
     * to VideoRecordEvent for the current recording status.
     */
    @SuppressLint("MissingPermission")
    private fun startRecording() {
        // create MediaStoreOutputOptions for our recorder: resulting our recording!

        val format = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        mCurrentFile =
            File(getExternalFilesDir(null)?.toString() + "/VID_" + format + ".mp4")
        val serviceOutputOptions = FileOutputOptions.Builder(mCurrentFile!!).build()
        val name = "VID_$format.mp4"

        Log.d("tag ", mCurrentFile!!.absolutePath+" path")


        if (isPurchase) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    put(
                        MediaStore.Video.Media.RELATIVE_PATH,
                        "${Environment.DIRECTORY_MOVIES}${File.separator}${getString(R.string.app_name)}"
                    )
                }
            }
            val mediaStoreOutput = MediaStoreOutputOptions.Builder(
                contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            )
                .setContentValues(contentValues)
                .build()

            // configure Recorder and Start recording to the mediaStoreOutput.
            currentRecording = videoCapture.output
                .prepareRecording(this, mediaStoreOutput)
                .apply { if (audioEnabled) withAudioEnabled() }
                .start(mainThreadExecutor, captureListener)

        } else {
            currentRecording = videoCapture.output
                .prepareRecording(this, serviceOutputOptions)
                .apply { if (audioEnabled) withAudioEnabled() }
                .start(mainThreadExecutor, captureListener)
        }


        Log.i(TAG, "Recording started")
    }


    /**
     * CaptureEvent listener.
     */
    private val captureListener = Consumer<VideoRecordEvent> { event ->
        // cache the recording state
        if (event !is VideoRecordEvent.Status)
            recordingState = event

        updateUI(event)

        if (event is VideoRecordEvent.Finalize) {
            // display the captured video
            lifecycleScope.launch {
                Log.d("ffnet", ": Done finalization")

            }
        }
    }


    /* UpdateUI according to CameraX VideoRecordEvent type:
    *   - user starts capture.
    *   - this app disables all UI selections.
    *   - this app enables capture run-time UI (pause/resume/stop).
    *   - user controls recording with run-time UI, eventually tap "stop" to end.
    *   - this app informs CameraX recording to stop with recording.stop() (or recording.close()).
    *   - CameraX notify this app that the recording is indeed stopped, with the Finalize event.
    *   - this app starts VideoViewer fragment to view the captured result.
    */
    private fun updateUI(event: VideoRecordEvent) {
        val state = if (event is VideoRecordEvent.Status) recordingState?.getNameString()
        else event.getNameString()
        when (event) {
            is VideoRecordEvent.Status -> {
                // placeholder: we update the UI with new status after this when() block,
                // nothing needs to do here.
            }
            is VideoRecordEvent.Start -> {
                showUI(UiState.RECORDING, event.getNameString())
            }
            is VideoRecordEvent.Finalize -> {
                showUI(UiState.FINALIZED, event.getNameString())
            }
            is VideoRecordEvent.Pause -> {

            }
            is VideoRecordEvent.Resume -> {

            }
        }

        val stats = event.recordingStats
        val size = stats.numBytesRecorded / 1000
        val time = java.util.concurrent.TimeUnit.NANOSECONDS.toSeconds(stats.recordedDurationNanos)
        var text = "${state}: recorded ${size}KB, in ${time}second"
        if (event is VideoRecordEvent.Finalize)
            text = "${text}\nFile saved to: ${event.outputResults.outputUri}"

        captureLiveStatus.value = text
        Log.i(TAG, "recording event: $text")
    }


    /**
     * One time initialize for CameraFragment (as a part of fragment layout's creation process).
     * This function performs the following:
     *   - initialize but disable all UI controls except the Quality selection.
     *   - set up the Quality selection recycler view.
     *   - bind use cases to a lifecycle camera, enable UI controls.
     */
    private fun initVideoRecording() {
        initializeUI()
        lifecycleScope.launch {

            if (enumerationDeferred != null) {
                enumerationDeferred!!.await()
                enumerationDeferred = null
            }
            initializeQualitySectionsUI()
            bindCaptureUsecase()
            checkTimeLimit()
        }


    }

    private fun checkTimeLimit() {

        val string =
            settings.recordingDuration
        string?.let {
                Log.e(
                    "CountDownTime",
                    TimeUnit.MINUTES.toMillis(string.toLong()).toString() + ""
                )
                countDownTimer =
                    object :
                        CountDownTimer(TimeUnit.MINUTES.toMillis(string.toLong()), 1000) {

                        override fun onTick(j: Long) {
                            Log.e("CountDownTime", "seconds remaining " + j / 1000)
                        }

                        override fun onFinish() {
                            Log.e("CountDownTime", "Finish")
                            try {
                                postInitialValues()
                                stopForeground(true)
                                stopSelf()
                                stopService()
                            } catch (e: Exception) {
                            }

                        }
                    }.start()

        }
    }

}













