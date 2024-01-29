package com.dt.auto.background.video.recorder.ads_integration.facebook

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.ads.*
import com.facebook.shimmer.ShimmerFrameLayout
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.ads_integration.admob.AdsResponse
import com.dt.auto.background.video.recorder.helpers.utils.gone

object FacebookNative {

    public fun load_Native_Fb(adLoading: TextView, adContainer: FrameLayout, shimmerFrameLayout: ShimmerFrameLayout, activity: AppCompatActivity,
                               adsResponse: AdsResponse) {

        val fbNativeAd = NativeAd(activity, "1387512678377513_1387514645043983")

        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {}
            override fun onError(ad: Ad?, adError: AdError) {
                Log.e("FaceBook", "Native ad failed to load: " + adError.getErrorMessage())

                adsResponse.isApplovinNativeShow(false)

            }

            override fun onAdLoaded(ad: Ad) {
                try {
                   /* pre_layout.setVisibility(View.GONE)
                    fb_ad_view.setVisibility(View.VISIBLE)*/
//                    if (fbNativeAd !== ad) {
//                        return
//                    }
                    Log.e("FaceBook", "loaded")
                    val adView: View = NativeAdView.render(activity, fbNativeAd)
                    adContainer.addView(
                        adView,
                        LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )

                    adsResponse.isApplovinNativeShow(true)
                    adLoading.gone()
                    shimmerFrameLayout.gone()
                } catch (ex: Exception) {
                    Log.d("FaceBook", "loaded$ex")
                }
            }

            override fun onAdClicked(ad: Ad) {}
            override fun onLoggingImpression(ad: Ad) {}
        }
        fbNativeAd.loadAd(
            fbNativeAd.buildLoadAdConfig()
                .withAdListener(nativeAdListener)
                .build())

    }



}