package com.dt.auto.background.video.recorder.app_ui.activity.applock

import android.content.Intent
import android.os.Bundle
import com.dt.auto.background.video.recorder.base.BaseActivity
import com.dt.auto.background.video.recorder.databinding.ActivityPinSettingBinding
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.helpers.utils.viewBinding

class PinSettingActivity : BaseActivity() {

    private val binding by viewBinding(ActivityPinSettingBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

       /* if (NetworkHelper.hasInternetConnection(this) && !BillingPurchases.isPurchase) {
            AdmobInterstitial.loadAdInterstitial(this)

        }*/
        //showBanner()

        handleClicks()
    }

    private fun handleClicks() {
        binding.apply {

            binding.showPreview.isChecked=settings.isPinEnabled

            showPreview.setCheckedChangedListener {

                if (it) {
                    binding.showPreview.hint = "Enabled"
                    binding.showPreview.isChecked = true
                    startActivity(Intent(this@PinSettingActivity, SimplePinLockActivity::class.java))

                   /* if (NetworkHelper.hasInternetConnection(this@PinSettingActivity)
                        && !BillingPurchases.isPurchase) {

                        if (AdCheckVariables.LOAD_FACEBOOK_INTER) {
                            FacebookInterstitials.showAd()

                        } else if (AdCheckVariables.LOAD_ADMOB_INTER) {
                            AdmobInterstitial.showInterstitialAd(this@PinSettingActivity)

                        } else if (AdCheckVariables.LOAD_APPLOVIN_INTER) {
                            ApplovinInterstitials.showAd()

                        }
                    }*/
                } else {
                    binding.showPreview.hint = "Disabled"
                    binding.showPreview.isChecked = false
                    settings.apply {
                        isPinEnabled = false
                    }
                }
            }


        }
    }

}