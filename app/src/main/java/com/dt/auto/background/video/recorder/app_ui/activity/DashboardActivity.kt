package com.dt.auto.background.video.recorder.app_ui.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.permissionx.guolindev.PermissionX
import com.dt.auto.background.video.recorder.AdCheckVariables
import com.dt.auto.background.video.recorder.BuildConfig
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.ads_integration.admob.AdmobInterstitial
import com.dt.auto.background.video.recorder.ads_integration.app_lovin.ApplovinInterstitials
import com.dt.auto.background.video.recorder.base.BaseActivity
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.helpers.FormattedTimeHelper.getFormattedStopWatchTime
import com.dt.auto.background.video.recorder.helpers.NetworkHelper.hasInternetConnection
import com.dt.auto.background.video.recorder.app_ui.activity.exit_screen.ExitAppScreen.Companion.startExitRecorderAppActivity
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.isPurchase
import com.dt.auto.background.video.recorder.app_ui.in_apps.InAppScreen
import com.dt.auto.background.video.recorder.app_ui.activity.applock.PinSettingActivity
import com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog.OverlayDialogFragment
import com.dt.auto.background.video.recorder.app_ui.activity.settings.ui.SettingsActivity
import com.dt.auto.background.video.recorder.app_ui.activity.video_recording.RecordedVideoScreen
import com.dt.auto.background.video.recorder.databinding.LayoutHomeFragmentBinding
import com.dt.auto.background.video.recorder.helpers.inAppUpdate
import com.dt.auto.background.video.recorder.service.NoScreenVideoRecorderService
import com.dt.auto.background.video.recorder.service.NoScreenVideoRecorderService.Companion.timeRunInMillis
import com.dt.auto.background.video.recorder.helpers.utils.*
import com.dt.auto.background.video.recorder.helpers.utils.Constants.ACTION_START_SERVICE
import com.dt.auto.background.video.recorder.helpers.utils.Constants.ACTION_STOP_SERVICE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DashboardActivity : BaseActivity() ,OverlayDialogFragment.Listener{

    companion object {
        var isInterAd_Show = false
    }

    private val binding by viewBinding(LayoutHomeFragmentBinding::inflate)
    private var isTracking = false
    private var curTimeInMillis = 0L

    var overlayDialogFragment: OverlayDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        logEvent("dashboard_activity")

        if (hasInternetConnection(this@DashboardActivity)) {
            inAppUpdate(this@DashboardActivity)
        }

       /* if (hasInternetConnection(this) && !isPurchase) {
            AdmobInterstitial.loadAdInterstitial(this@DashboardActivity)
        }*/

        binding.timerTv.text = "00:00:00"
        handleClickListeners()
        subscribeToObservers()

        //showNativeAds()


    }

    private fun handleClickListeners() {


        binding.ibPlay.setOnClickListenerCoolDown {
//            vibrateOnClick()
            lifecycleScope.launch {


                if (!Settings.canDrawOverlays(applicationContext)){
                            showOverlayDialog()
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        PermissionX.init(this@DashboardActivity)
                            .permissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_MEDIA_VIDEO
                            )
                            .request { allGranted, grantedList, deniedList ->
                                if (allGranted) {
                                    binding.ibPlay.invisible()
                                    binding.stop.visible()
                                    sendCommandToService(ACTION_START_SERVICE)
                                }
                            }
                    }
                    else {

                        PermissionX.init(this@DashboardActivity)
                            .permissions(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO
                            )
                            .request { allGranted, grantedList, deniedList ->
                                if (allGranted) {
                                    binding.ibPlay.invisible()
                                    binding.stop.visible()
                                    sendCommandToService(ACTION_START_SERVICE)
                                }
                            }

                    }
                }

            }


        }

        binding.stop.setOnClickListenerCoolDown {
            vibrateOnClick()
            sendCommandToService(ACTION_STOP_SERVICE)
            binding.ibPlay.visible()
            binding.stop.invisible()
            binding.timerTv.text = "00:00:00"
            if (hasInternetConnection(this@DashboardActivity) && !isPurchase) {
                lifecycleScope.launch {
//                    showDelayDialog()
                    delay(1000)
//                    dismissDelayDialog()
                    toastInfo(getString(R.string.saved_success))
                   /* if (AdCheckVariables.LOAD_FACEBOOK_INTER){
                        FacebookInterstitials.showAd()

                    }else*/
                    isInterAd_Show = true

                    if(AdCheckVariables.LOAD_ADMOB_INTER){
                        AdmobInterstitial.showInterstitialAd(this@DashboardActivity)

                    }else if(AdCheckVariables.LOAD_APPLOVIN_INTER){
                        ApplovinInterstitials.showAd()
                    }

                    //showInterstitialEitherAdmobOrAppLovinInDashboard()
                }
            } else {
                toastInfo(getString(R.string.saved_success))
            }

        }


        binding.ivPro.setOnClickListenerCoolDown {
           // startActivity(Intent(this@DashboardActivity, InAppScreen::class.java))
        }



        binding.layoutSaved.setOnClickListenerCoolDown {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionX.init(this)
                    .permissions( Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_VIDEO)
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            if (hasInternetConnection(this@DashboardActivity) && !isPurchase && !isInterAd_Show) {
                                lifecycleScope.launch {
//                            showDelayDialog()
                                    delay(1000)
//                            dismissDelayDialog()

                                    startActivity(
                                        Intent(
                                            this@DashboardActivity,
                                            RecordedVideoScreen::class.java
                                        )
                                    )
                                    /* if (AdCheckVariables.LOAD_FACEBOOK_INTER){
                                     FacebookInterstitials.showAd()

                                 }else*/ if (AdCheckVariables.LOAD_ADMOB_INTER) {
                                    AdmobInterstitial.showInterstitialAd(this@DashboardActivity)

                                } else if (AdCheckVariables.LOAD_APPLOVIN_INTER) {
                                    ApplovinInterstitials.showAd()

                                }
                                    isInterAd_Show = true

                                }
                            } else {
                                isInterAd_Show = false
                                startActivity(
                                    Intent(
                                        this@DashboardActivity,
                                        RecordedVideoScreen::class.java
                                    )
                                )
                            }
                        }
                    }
            }else {

                PermissionX.init(this@DashboardActivity)
                    .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                    )
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            if (hasInternetConnection(this@DashboardActivity) && !isPurchase && !isInterAd_Show) {
                                lifecycleScope.launch {
//                            showDelayDialog()
                                    delay(1000)
//                            dismissDelayDialog()

                                    startActivity(
                                        Intent(
                                            this@DashboardActivity,
                                            RecordedVideoScreen::class.java
                                        )
                                    )
                                    /* if (AdCheckVariables.LOAD_FACEBOOK_INTER){
                                     FacebookInterstitials.showAd()

                                 }else*/ if (AdCheckVariables.LOAD_ADMOB_INTER) {
                                    AdmobInterstitial.showInterstitialAd(this@DashboardActivity)

                                } else if (AdCheckVariables.LOAD_APPLOVIN_INTER) {
                                    ApplovinInterstitials.showAd()

                                }

                                    isInterAd_Show = true
                                }
                            } else {
                                isInterAd_Show = false
                                startActivity(
                                    Intent(
                                        this@DashboardActivity,
                                        RecordedVideoScreen::class.java
                                    )
                                )
                            }
                        }
                    }
            }
        }


        binding.layoutApplock.setOnClickListenerCoolDown {
            startActivity(Intent(this@DashboardActivity, PinSettingActivity::class.java))
        }

        binding.layoutSetting.setOnClickListenerCoolDown {
            startActivity(Intent(this@DashboardActivity, SettingsActivity::class.java))
        }

        binding.layoutPreview.setOnClickListenerCoolDown {
            settings.hasPreview = true
            startActivity(Intent(this@DashboardActivity, SettingsActivity::class.java))
        }

        binding.btnShowCameraPreview.setOnClickListenerCoolDown {
            settings.hasPreview = true
            startActivity(Intent(this@DashboardActivity, SettingsActivity::class.java))
        }

        binding.btnShowCameraPreview2.setOnClickListenerCoolDown {
            settings.hasPreview = false
            startActivity(Intent(this@DashboardActivity, SettingsActivity::class.java))

        }

        binding.ibShare.setOnClickListenerCoolDown {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    """${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}""".trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(
                    Intent.createChooser(
                        shareIntent,
                        getString(R.string.share_using)
                    )
                )
            } catch (e: Exception) {

            }

        }

    }

    private fun showOverlayDialog() {
        overlayDialogFragment = OverlayDialogFragment.newInstance()
        overlayDialogFragment?.show(supportFragmentManager, "")

    }


    private fun subscribeToObservers() {

        NoScreenVideoRecorderService.isTracking.observe(this) { updateTracking(it) }

        timeRunInMillis.observe(this) {
            curTimeInMillis = it
            val formattedTime = getFormattedStopWatchTime(curTimeInMillis, true)
            binding.timerTv.text = formattedTime
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking

        if (!isTracking) {
            binding.ibPlay.visible()
            binding.stop.invisible()
            binding.timerTv.text = "00:00:00"
        } else {
            binding.ibPlay.invisible()
            binding.stop.visible()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(applicationContext)) {

                startActivityForResult(
                    Intent(
                        "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                        Uri.parse("""package:${BuildConfig.APPLICATION_ID}""")
                    ), 123
                )
                return
            }
        }

    }

    private fun sendCommandToService(action: String) =
        Intent(
            this@DashboardActivity,
            NoScreenVideoRecorderService::class.java
        ).also {
            it.action = action
            startService(it)
        }

    override fun onBackPressed() {
        startExitRecorderAppActivity(this@DashboardActivity)
    }

    override fun onPermissionCheck() {
        checkDrawOverlayPermission()
    }

    override fun onResume() {
        super.onResume()
        try {
           /* if(AppOpenManager.isShowingAd) {
                hideNativeAd()
                binding.constraintLayout.visibility=View.GONE
            }
            else {
                visibleNativeAd()
                binding.constraintLayout.visibility=View.VISIBLE
            }*/

        } catch (e: Exception) {
        }

    }
}