package com.dt.auto.background.video.recorder.app_ui.activity.settings.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.dt.auto.background.video.recorder.BuildConfig
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.base.BaseActivity
import com.dt.auto.background.video.recorder.databinding.ActivitySettingsBinding
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.app_ui.activity.quality_selection.VideoQualitySelectionActivity
import com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog.CameraSelectionDialogFragment
import com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog.DurationSelectionDialogFragment
import com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog.ScreenSelectionDialogFragment
import com.dt.auto.background.video.recorder.helpers.utils.*


class SettingsActivity : BaseActivity(), CameraSelectionDialogFragment.Listener,
    ScreenSelectionDialogFragment.Listener, DurationSelectionDialogFragment.Listener {

    companion object {
        var comingFromSettingsActivity = false
    }

    private val binding by viewBinding(ActivitySettingsBinding::inflate)

    var dialogCameraSelection: CameraSelectionDialogFragment? = null
    var dialogScreenSelection: ScreenSelectionDialogFragment? = null
    var dialogRecordDurationSelection: DurationSelectionDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        logEvent("settings_activity")

        showSettings()
        handleClicks()
       // showNativeAds()
        binding.versionCheckButton.hint = BuildConfig.VERSION_NAME

    }


    private fun handleClicks() {
        binding.apply {


            videoQualitySelectionBtn.setOnClickListenerCoolDown {
                startActivity(
                    Intent(
                        this@SettingsActivity,
                        VideoQualitySelectionActivity::class.java
                    )
                )
            }


            if (settings.hasPreview) {
                binding.showPreview.hint = "Enabled"
                binding.showPreview.isChecked = true
            } else {
                binding.showPreview.hint = "Disabled"
                binding.showPreview.isChecked = false

            }

            showPreview.setCheckedChangedListener {
                settings.apply {
                    hasPreview = it
                }
                if (it) {
                    binding.showPreview.hint = "Enabled"
                    binding.showPreview.isChecked = true
                } else {
                    binding.showPreview.hint = "Disabled"
                    binding.showPreview.isChecked = false

                }
            }
            selectCameraBtn.setOnClickListenerCoolDown { showCameraSelectionDialog() }
            previewButton.setOnClickListenerCoolDown { showScreenSelectionDialog() }
            recordingDurationButton.setOnClickListenerCoolDown { showRecordingDurationDialog() }


            privacyPolicyBtn.setOnClickListenerCoolDown {
                comingFromSettingsActivity = true
//                startActivity(
//                    Intent(
//                        this@SettingsActivity,
//                        PrivacyPolicyCheckActivity::class.java
//                    )
//                )
            }
        }

        binding.rateUsBtn.setOnClickListenerCoolDown {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                    )
                )
            }
        }

        binding.shareUsBtn.setOnClickListenerCoolDown {
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

        binding.scheduleVideoButton.setOnClickListenerCoolDown {
            startActivity(Intent(this@SettingsActivity, VideoScheduleActivity::class.java))
        }


    }

    private fun showSettings() {
        settings.apply {
            /*
            * Preview done
            * */
            if (hasPreview) {
                binding.showPreview.hint = "Enabled"
                binding.showPreview.isChecked = true
            } else {
                binding.showPreview.hint = "Disabled"
                binding.showPreview.isChecked = false

            }

            /*
            * Camera
            * */
//            if (isBackCamera?.equals(getString(R.string.front_camera)) == true) {
//                binding.selectCameraBtn.hint = getString(R.string.front_camera)
//            } else {
//                binding.selectCameraBtn.hint = getString(R.string.back_camera)
//            }

            if (isBackCameraInt == 1) {
                binding.selectCameraBtn.hint = getString(R.string.front_camera)
            } else {
                binding.selectCameraBtn.hint = getString(R.string.back_camera)
            }

            /*
            * Preview
            * */
            if (previewSize?.equals(getString(R.string.medium)) == true) {
                binding.previewButton.hint = getString(R.string.medium)
            } else if (previewSize?.equals(getString(R.string.small)) == true) {
                binding.previewButton.hint = getString(R.string.small)
            } else if (previewSize?.equals(getString(R.string.large)) == true) {
                binding.previewButton.hint = getString(R.string.large)
            }

            /*
            * Duration
            * */
            if (recordingDuration.equals(getString(R.string.unlimited))) {
                binding.recordingDurationButton.hint = getString(R.string.unlimited)
            } else if (recordingDuration != getString(R.string.unlimited)) {
                binding.recordingDurationButton.hint = "$recordingDuration mins"
            }

        }
    }


    private fun showCameraSelectionDialog() {
        dialogCameraSelection = CameraSelectionDialogFragment.newInstance()
        dialogCameraSelection?.show(supportFragmentManager, "")
    }

    private fun showScreenSelectionDialog() {
        dialogScreenSelection = ScreenSelectionDialogFragment.newInstance()
        dialogScreenSelection?.show(supportFragmentManager, "")
    }

    private fun showRecordingDurationDialog() {
        dialogRecordDurationSelection = DurationSelectionDialogFragment.newInstance()
        dialogRecordDurationSelection?.show(supportFragmentManager, "")
    }

    override fun onCancelConfirmed() {
        dialogCameraSelection?.dismiss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        dialogCameraSelection?.dismiss()
        dialogScreenSelection?.dismiss()
        dialogRecordDurationSelection?.dismiss()
    }

    override fun onCancelScreenSelectionConfirmed() {
        dialogScreenSelection?.dismiss()
    }

    override fun OnCameraSelection() {
        showSettings()
    }

    override fun onScreenSelection() {
        showSettings()
    }

    override fun onDoneDuration() {
        showSettings()
        dialogRecordDurationSelection?.dismiss()
    }


    override fun onResume() {
        super.onResume()
        try {


           /* if(AppOpenManager.isShowingAd)
                hideNativeAd()
            else
                visibleNativeAd()*/

        } catch (e: Exception) { }

    }
}