package com.dt.auto.background.video.recorder.ads_integration.admob

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.dt.auto.background.video.recorder.R
import com.google.android.gms.ads.*
import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.bannerAdId
import com.dt.auto.background.video.recorder.helpers.utils.gone


object BannersImplementation {

    /*
    * Change
    * */
    fun showBannerAd(
        activity: AppCompatActivity, adContainer: FrameLayout,
        shimmerFrameLayout: ShimmerFrameLayout, adSize: Int, adsResponse: AdsResponse
    ) {
        try {
            val bannerId: String = bannerAdId ?: activity.getString(R.string.admob_banner_ad)
            val adRequest: AdRequest = AdRequest.Builder().build()
            val adView = AdView(activity)
            if (adSize == 1) adView.setAdSize(getAdSize(activity)) else if (adSize == 2) adView.setAdSize(
                AdSize.LARGE_BANNER
            ) else adView.setAdSize(AdSize.MEDIUM_RECTANGLE)

            adView.adUnitId = bannerId

            adView.adListener = object : AdListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    //set adsResponses false to show another ads request from app-lovin

                    adsResponse.isApplovinBannerShow(false)
                    //adsResponse.isFacebookBannerShow(false)
                    Log.d("ffnet", "Failed load banner:${loadAdError.message}")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    //set adsResponses true to show ads

                    adsResponse.isFacebookBannerShow(true)
                    adContainer.removeAllViews()
                    adContainer.addView(adView)
                    shimmerFrameLayout.gone()

                }

                override fun onAdOpened() {
                    super.onAdOpened()
                }
            }
            adView.loadAd(adRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getAdSize(context: Activity): AdSize {
        val display: Display = context.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

}