@file:Suppress("DEPRECATION")
package com.dt.auto.background.video.recorder.app_ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.isPurchase
import com.dt.auto.background.video.recorder.app_ui.activity.applock.SimplePinLockActivity
import com.dt.auto.background.video.recorder.base.BaseActivity
import com.dt.auto.background.video.recorder.databinding.LayoutSplashOneFragmentBinding
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.helpers.MY_REQUEST_CODE
import com.dt.auto.background.video.recorder.helpers.NetworkHelper.hasInternetConnection
import com.dt.auto.background.video.recorder.helpers.inAppUpdate
import com.dt.auto.background.video.recorder.helpers.utils.gone
import com.dt.auto.background.video.recorder.helpers.utils.setOnClickListenerCoolDown
import com.dt.auto.background.video.recorder.helpers.utils.viewBinding
import com.dt.auto.background.video.recorder.helpers.utils.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : BaseActivity() {


    private val MoveFromSplash: String="MoveFromSplash"
    private val binding by viewBinding(LayoutSplashOneFragmentBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        logEvent("splash_activity")

        lifecycleScope.launch {
            delay(500)

            if(isPurchase) {
                if (!settings.isPinEnabled) {
                    startActivity(
                        Intent(this@SplashScreen, DashboardActivity::class.java))
                } else {
                    startActivity(
                        Intent(this@SplashScreen, SimplePinLockActivity::class.java).putExtra(
                            "PIN_LOCK",
                            1
                        )
                    )
                }
                finish()
            }/*else if (hasInternetConnection(this@SplashScreen) && !isPurchase) {

                AdmobInterstitial.loadAdInterstitialAdmobSplash(this@SplashScreen)
                ApplovinInterstitials.loadApplovinInterstitial(this@SplashScreen)

            }*/


        }


        if (hasInternetConnection(this@SplashScreen)) {
            inAppUpdate(this@SplashScreen)
        }



        lifecycleScope.launchWhenCreated {
            delay(7000)
            binding.progress.gone()
            binding.moveToActivity.visible()
        }

        binding.moveToActivity.setOnClickListenerCoolDown {

            lifecycleScope.launch {
                if(!settings.isPinEnabled) {
                  /*  if (hasInternetConnection(this@SplashScreen) && !isPurchase) {
                        startActivity(Intent(this@SplashScreen, InAppScreen::class.java)
                            .putExtra(MoveFromSplash,true))
                        //showInterstitialSplash()
                    }else {*/
                        startActivity(Intent(this@SplashScreen, DashboardActivity::class.java))
                    //}
                    finish()
                }
                else{
                    startActivity(Intent(this@SplashScreen, SimplePinLockActivity::class.java).putExtra("PIN_LOCK",1))
                }
            }

        }

        binding.txtGetPro.setOnClickListenerCoolDown {
            //startActivity(Intent(this@SplashScreen, InAppScreen::class.java))
        }


    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                inAppUpdate(this@SplashScreen)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        try {
           /* showNativeAds()
           *//* if(!isPurchase)
                binding.txtGetPro.visibility= View.GONE
*//*
            if(AppOpenManager.isShowingAd)
                hideNativeAd()
            else
                visibleNativeAd()*/

        } catch (e: Exception) {
        }
    }

}
