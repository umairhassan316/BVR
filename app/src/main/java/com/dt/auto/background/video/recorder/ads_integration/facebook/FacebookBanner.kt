package com.dt.auto.background.video.recorder.ads_integration.facebook

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.facebook.ads.Ad
import com.facebook.ads.AdListener
import com.facebook.ads.AdView
import com.facebook.shimmer.ShimmerFrameLayout
import com.dt.auto.background.video.recorder.ads_integration.admob.AdsResponse

object FacebookBanner {

    /*
    * Change
    * */
    fun showBannerAd(
        activity: AppCompatActivity, adContainer: FrameLayout,
        shimmerFrameLayout: ShimmerFrameLayout, adSize: Int, adsResponse: AdsResponse
    ) {
        try {
            //fb_banner_area.setVisibility(View.VISIBLE)

            val adListener: AdListener = object : AdListener {

                override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                    Log.d("FbBanner", "error " + p1?.errorMessage)
                    /* bannerArea.setVisibility(View.VISIBLE)
                     loadBannerAdmob(Values.admob_banner_id)*/
                    adsResponse.isApplovinBannerShow(false)
                }

                override fun onAdLoaded(ad: Ad) {
                    adsResponse.isApplovinBannerShow(true)
                    shimmerFrameLayout.visibility= View.GONE
                }
                override fun onAdClicked(ad: Ad) {}
                override fun onLoggingImpression(ad: Ad) {}
            }

            val fbBannerView =
                AdView(activity, "1387512678377513_1387513665044081", com.facebook.ads.AdSize.BANNER_HEIGHT_90)
            adContainer.addView(fbBannerView)
            fbBannerView.loadAd(fbBannerView.buildLoadAdConfig().withAdListener(adListener).build())

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}