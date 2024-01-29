package com.dt.auto.background.video.recorder.app_ui.activity.video_recording

import android.Manifest
import android.app.RecoverableSecurityException
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.SearchView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dt.auto.background.video.recorder.BuildConfig
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.app_ui.adapter.RecordingsListAdapter
import com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog.VideoDeletionDialogFragment
import com.dt.auto.background.video.recorder.base.BaseActivity
import com.dt.auto.background.video.recorder.databinding.ActivityVideoRecordingsListBinding
import com.dt.auto.background.video.recorder.domain.RecordingsDto
import com.dt.auto.background.video.recorder.helpers.VersionCheckHelper.isNougatPlus
import com.dt.auto.background.video.recorder.helpers.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class RecordedVideoScreen : BaseActivity(), RecordingsListAdapter.OnItemClickListener,
    VideoDeletionDialogFragment.Listener,
    SearchView.OnQueryTextListener {
    /*
    * Listeners added here to share, delete and play the recordings done
    * */
    override fun setOnItemPlayClickListener(position: Int, item: RecordingsDto) {
        toastInfo("Please wat!")
        MediaScannerConnection.scanFile(this, arrayOf(item.contentUri.path.toString()), null, null)

        try {
            startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                type = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(".mp4")
                val authority = "${BuildConfig.APPLICATION_ID}.provider"
                data = FileProvider.getUriForFile(
                    this@RecordedVideoScreen,
                    authority,
                    File(item.contentUri.path.toString())
                )
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        } catch (e: Exception) {
            log("Error: ${e.localizedMessage}")
            toastInfo("Error Playing Video")
        }
    }

    var itemToDelete: RecordingsDto? = null

    override fun setOnItemDeleteClickListener(position: Int, item: RecordingsDto) {

        lifecycleScope.launch {
            itemToDelete = item
            showVideoDeletionConfirmationDialog()
//            deletePhotoFromExternalStorage(Uri.parse("content:${item.contentUri}"))

        }
    }

    override fun setOnItemShareClickListener(position: Int, item: RecordingsDto) {
        try {
            val videoFile: File = File(item.filepath)
            val videoURI =
                if (isNougatPlus()) FileProvider.getUriForFile(
                    this,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    videoFile
                ) else Uri.fromFile(videoFile)
            ShareCompat.IntentBuilder(this)
                .setStream(videoURI)
                .setType("video/mp4")
                .setChooserTitle("Share video...")
                .startChooser()
        } catch (e: Exception) {
            log("Error: ${e.localizedMessage}")
            toastError(R.string.error_sharing_video)
        }
    }


    companion object {
        var RECORDINGS_LIST = listOf<RecordingsDto>()
    }

    private val binding by viewBinding(ActivityVideoRecordingsListBinding::inflate)

    private val recordingListAdapter by lazy {
        RecordingsListAdapter(
            this
        )
    }

    private var readPermissionGranted = false
    private var writePermissionGranted = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var contentObserver: ContentObserver

    var videoDeletionDialogFragment: VideoDeletionDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        logEvent("video_recordings_list")

        setupRecyclerViewAdapter()

        initContentObserver()
        loadVideosFromExternalStorageIntoRecyclerView()
        permissionIntentSetup()
//        updateOrRequestPermissions()


        //showBanner()
    }

    private fun permissionIntentSetup() {
        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermissionGranted =
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
                writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermissionGranted

                if (readPermissionGranted) {
                    loadVideosFromExternalStorageIntoRecyclerView()
                } else {
                    toastWarning(R.string.enable_permissions)

                }
            }
    }

    private fun loadVideosFromExternalStorageIntoRecyclerView() {

        lifecycleScope.launch {
            val videos = loadVideosFromExternalStorage()
            RECORDINGS_LIST = videos
            binding.progress.isVisible = videos.isEmpty()
            binding.noVideos.isVisible = videos.isEmpty()
            binding.recordingsRv.isVisible = videos.isNotEmpty()
            recordingListAdapter.submitList(videos)
        }

        lifecycleScope.launch {
            delay(3000)
            if (binding.noVideos.isVisible) {
                binding.progress.invisible()
            }
        }

    }


    private fun setupRecyclerViewAdapter() {

        val linearLayoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
            reverseLayout = true
            stackFromEnd = true
        }


        binding.recordingsRv.apply {
            adapter = recordingListAdapter
            layoutManager?.isItemPrefetchEnabled = true
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            itemAnimator?.changeDuration = 0

        }

        binding.searchView.setOnQueryTextListener(this)
    }


    private fun initContentObserver() {
        contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                if (readPermissionGranted) {
                    loadVideosFromExternalStorageIntoRecyclerView()
                }
            }
        }
        contentResolver.registerContentObserver(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    private suspend fun loadVideosFromExternalStorage(): List<RecordingsDto> {
        return withContext(Dispatchers.IO) {
            val photos = mutableListOf<RecordingsDto>()

            photos.clear()
            /*val file = if (isPurchase) {
                 File(
                     Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath + "/${
                         getString(R.string.app_name)
                     }"
                 )
             } else {
                 File(getExternalFilesDir(null)?.absolutePath.toString())

             }*/

            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath + "/${
                    getString(R.string.app_name)
                }"
            )

            val listdir = File(getExternalFilesDir(null)?.absolutePath.toString())

            val fileList = if (listdir.listFiles().size > 0) {
                listdir.listFiles().toList()
            } else {
                emptyList()
            }

            val listFiles = fileList.toMutableList()

// Add files from 'file' if it is defined and has files
            file?.let { file ->
                if (file.exists() && file.isDirectory) {
                    listFiles.addAll(file.listFiles())
                }
            }

// Print the list of files
            listFiles.forEach { file ->
                println(file.name)
            }




            try {
                listFiles.map { fileCheck ->

                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    mediaMetadataRetriever.setDataSource(Uri.decode(fileCheck.path))
                    val parseLong = mediaMetadataRetriever.extractMetadata(9)!!.toLong()
                    val format_ = String.format(
                        "%02d:%02d:%02d", java.lang.Long.valueOf(
                            TimeUnit.MILLISECONDS.toHours(parseLong)
                        ), java.lang.Long.valueOf(
                            TimeUnit.MILLISECONDS.toMinutes(parseLong) % 60
                        ), java.lang.Long.valueOf(
                            TimeUnit.MILLISECONDS.toSeconds(parseLong) % 60
                        )
                    )

                    photos += RecordingsDto(
                        id = -1,
                        datetime = SimpleDateFormat("dd-MM-yyyy, hh:mm aaa").format(
                            Date(
                                File(
                                    fileCheck.path
                                ).lastModified()
                            )
                        ),
                        duration = format_,
                        filename = fileCheck.name,
                        filepath = fileCheck.path,
                        size = formatFileSize(fileCheck.length()),
                        contentUri = Uri.parse(File(fileCheck.absolutePath).toString())
                    )


                }
            } catch (e: Exception) {
            }
            photos.toList()
        } ?: listOf()

    }


    private suspend fun deletePhotoFromExternalStorage(photoUri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                contentResolver.delete(photoUri, null, null)
                loadVideosFromExternalStorageIntoRecyclerView()

            } catch (e: SecurityException) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(
                            contentResolver,
                            listOf(photoUri)
                        ).intentSender
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverableSecurityException = e as? RecoverableSecurityException
                        recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }
                intentSender?.let { sender ->
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(sender).build()
                    )
                }
            }
        }
    }

    private val BYTES = " Bytes"
    private val GIGA: Long = 1073741824
    private val GIGABYTES = " GB"
    private val KILOBYTES = " KB"
    private val MEGABYTES = " MB"

    private fun formatFileSize(j: Long): String {
        return if (j < 1024) {
            j.toString() + BYTES
        } else if (j < 1048576) {
            (j.toDouble() / 1024.0 + 0.5).toInt()
                .toString() + KILOBYTES
        } else if (j < GIGA) {
            (j.toDouble() / 1048576.0 + 0.5).toInt()
                .toString() + MEGABYTES
        } else {
            (j.toDouble() / 1.073741824E9 + 0.5).toInt()
                .toString() + GIGABYTES
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        (binding.recordingsRv.adapter as RecordingsListAdapter).filter.filter(newText)
        return true
    }


    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }


    private fun showVideoDeletionConfirmationDialog() {
        videoDeletionDialogFragment = VideoDeletionDialogFragment.newInstance()
        videoDeletionDialogFragment?.show(supportFragmentManager, "")
    }

    override fun onDeletionConfirmed() {
        File(itemToDelete?.contentUri?.path.toString()).deleteRecursively()
        loadVideosFromExternalStorageIntoRecyclerView()
        videoDeletionDialogFragment?.dismiss()
        toastInfo("Deleted ${itemToDelete?.filename.toString()} successfully")
    }

    override fun onCancelConfirmed() {
        videoDeletionDialogFragment?.dismiss()
    }


    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(contentObserver)
    }

}