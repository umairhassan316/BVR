package com.dt.auto.background.video.recorder.app_ui.in_apps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.isBpClientReady
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.launchBillingFlow
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.lifetimeSkuDetails
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.lifetimeprice
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.monthlyPrice
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.monthlySkuDetails
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.oneWeekPrice
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.oneWeekSkuDetails
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.threeMonthsPrice
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.threeMonthsSkuDetails
import com.dt.auto.background.video.recorder.app_ui.activity.DashboardActivity
import com.dt.auto.background.video.recorder.app_ui.activity.permission.PermissionActivity
import com.dt.auto.background.video.recorder.base.BaseActivity
import com.dt.auto.background.video.recorder.databinding.ActivityPurchaseBinding
import com.dt.auto.background.video.recorder.helpers.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class InAppScreen : BaseActivity() {



    private val binding by viewBinding(ActivityPurchaseBinding::inflate)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFullScreen()
        setContentView(binding.root)
        logEvent("in_app_activity")

        lifecycleScope.launchWhenCreated { setPricesAndTextFromInApp() }

        setUpOnClickListeners()

    }

    private fun setUpOnClickListeners() {

        val anim: Animation = AlphaAnimation(0.5f, 1.0f)
        anim.duration = 2000
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        binding.lifeTimeImage.startAnimation(anim)


        binding.month3Iv.setOnClickListenerCoolDown {
            try {
                if (isBpClientReady && threeMonthsSkuDetails != null) {
                    launchBillingFlow(this@InAppScreen, threeMonthsSkuDetails!!)
                } else {
                    toast(R.string.try_again_in_moment)
                }
            } catch (er: Exception) {
            }
        }
        binding.week4Iv.setOnClickListenerCoolDown {
            try {
                if (isBpClientReady && monthlySkuDetails != null) {
                    launchBillingFlow(this@InAppScreen, monthlySkuDetails!!)
                } else {
                    toast(R.string.try_again_in_moment)
                }
            } catch (er: Exception) {
            }
        }
        binding.week1Iv.setOnClickListenerCoolDown {
            try {
                if (isBpClientReady && monthlySkuDetails != null) {
                    launchBillingFlow(this@InAppScreen, oneWeekSkuDetails!!)
                } else {
                    toast(R.string.try_again_in_moment)
                }
            } catch (er: Exception) {
            }
        }

        binding.lifeTimeImage.setOnClickListenerCoolDown {
            try {
                if (isBpClientReady && lifetimeSkuDetails != null) {
                    launchBillingFlow(this@InAppScreen, lifetimeSkuDetails!!)
                } else {
                    toast(R.string.try_again_in_moment)
                }
            } catch (er: Exception) {
            }
        }

        binding.tvCancel.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/account/subscriptions")
            )
            startActivity(browserIntent)
        }

        binding.cancelBtn.setOnClickListenerCoolDown {
            lifecycleScope.launch {


                if (intent.getBooleanExtra(
                        "MoveFromSplash",
                        false) && checkPermission()) {
                    startActivity(Intent(this@InAppScreen, PermissionActivity::class.java))
                } else
                    startActivity(Intent(this@InAppScreen, DashboardActivity::class.java))
                finish()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
    }
    private fun setPricesAndTextFromInApp() {
        try {

            binding.lifeTimeTv.text = "${lifetimeprice}" ?: "null"
            binding.threeMonthsTv.text = "${threeMonthsPrice}/ \n 3 Month's" ?: "null"
            binding.oneMonthTv.text = "${monthlyPrice}/ \n Monthly" ?: "null"
            binding.oneWeekTv.text = "${oneWeekPrice}/ \n Weekly" ?: "null"

        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {}
}