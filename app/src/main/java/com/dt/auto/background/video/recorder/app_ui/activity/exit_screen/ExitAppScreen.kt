package com.dt.auto.background.video.recorder.app_ui.activity.exit_screen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import com.appizona.yehiahd.fastsave.FastSave
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.dt.auto.background.video.recorder.BuildConfig
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.base.BaseActivity
import com.dt.auto.background.video.recorder.databinding.ActivityExitAppLayoutBinding
import com.dt.auto.background.video.recorder.helpers.utils.setOnClickListenerCoolDown
import com.dt.auto.background.video.recorder.helpers.utils.viewBinding


class ExitAppScreen : BaseActivity() {

    companion object {
        fun startExitRecorderAppActivity(context: Context) {
            context.startActivity(Intent(context, ExitAppScreen::class.java))
        }
    }


    private val binding by viewBinding(ActivityExitAppLayoutBinding::inflate)


    private var btnSubmit: Button? = null
    private var btnCancel: Button? = null
    var ratingBar: RatingBar? = null
    var rateVal: Float? = null

    var frameLayout: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        logEvent("exit_activity")

        setupToolbar()
        initializeViews()
        setUpRating()

        //showInterstitial()
        //showNativeAds()
//        showNativeEitherAdmobOrAppLovinInExitScreen()
    }


    private fun setupToolbar() {


        binding.include3.exitBtn.setOnClickListenerCoolDown {
            FastSave.getInstance().saveBoolean("show_app_open", true)
            finishAffinity()
        }

        binding.include3.cancelButton.setOnClickListenerCoolDown { super.onBackPressed() }

        binding.include3.shareBtn.setOnClickListenerCoolDown { shareApp() }

    }

    private fun initializeViews() {
        btnSubmit = binding.include4.buttonReviewSubmit
        btnCancel = binding.include4.buttonReviewCancel
        ratingBar = binding.include3.rating
    }



    private fun setUpRating() {

        reviewManager = ReviewManagerFactory.create(this)

        ratingBar?.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar, v, b ->
            rateVal = ratingBar.rating


            if (rateVal!! >= 4) {
                showRateApp()
            }
            if (rateVal!! < 4) {
                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    val recipients = arrayOf(getString(R.string.rating_feedback_email))
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for ${getString(R.string.app_name)}")
                    intent.type = "text/html"
                    intent.setPackage("com.google.android.gm")
                    startActivity(Intent.createChooser(intent, "Send mail"))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                }
            }
        }


    }




    private fun shareApp() {
        try {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                """Hey check out ${getString(R.string.app_name)}: https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"""
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        } catch (e: Exception) {
        }
    }


    private var reviewManager: ReviewManager? = null

    private fun showRateApp() {
        val request: Task<ReviewInfo?> = reviewManager?.requestReviewFlow() as Task<ReviewInfo?>
        request.addOnCompleteListener { task: Task<ReviewInfo?> ->
            if (task.isSuccessful) {
                // We can get the ReviewInfo object
                val reviewInfo = task.result
                val flow: Task<Void?> =
                    reviewManager?.launchReviewFlow(this, reviewInfo) as Task<Void?>
                flow.addOnCompleteListener { task1: Task<Void?>? -> }
            } else {

                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
                redirectToPlayStore()
            }


        }
        Toast.makeText(this, "Thanks for your feedback.", Toast.LENGTH_SHORT).show()


    }


    private fun redirectToPlayStore() {

        try {
            Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                )
            )
        } catch (e: Exception) {
        }

    }

}