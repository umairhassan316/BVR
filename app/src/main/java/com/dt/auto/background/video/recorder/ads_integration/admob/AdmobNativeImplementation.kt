package com.dt.auto.background.video.recorder.ads_integration.admob

import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.ads_integration.facebook.FacebookNative
import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.nativeAdId
import com.dt.auto.background.video.recorder.helpers.utils.gone
import com.dt.auto.background.video.recorder.helpers.utils.visible


object AdmobNativeImplementation {

    var nativeAd: NativeAd? = null

    fun showNativeAdmob(adLoading: TextView, adContainer: FrameLayout, shimmerFrameLayout: ShimmerFrameLayout, activity: AppCompatActivity, adsResponse: AdsResponse)
    {
        var builder: AdLoader.Builder? = null
        builder = AdLoader.Builder(activity, nativeAdId ?:  activity.getString(R.string.admob_native_ad))

        builder.forNativeAd(OnNativeAdLoadedListener { nativeAdd: NativeAd ->


            nativeAd?.destroy() ?: run { nativeAd = nativeAdd }

            val adView = activity.layoutInflater.inflate(
                R.layout.layout_native_ad_design,
                null
            ) as NativeAdView

            populateNativeAd(nativeAdd, adView)

            adContainer.removeAllViews()
            adContainer.addView(adView)

        })
        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()
        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d("ffnet", "Failed load Admob native :${loadAdError.message}")
                //set adsResponses false to show another ads request from app-lovin
                //adsResponse.isFacebookNativeShow(false)

                //set adsResponses false to show another ads request from app-lovin
                adsResponse.isApplovinNativeShow(false)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()

                Log.d("ffnet", "Admob native:onAdLoaded")
                //set adsResponses true to show ads
                adsResponse.isFacebookNativeShow(true)
                adLoading.gone()
                shimmerFrameLayout.gone()
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }


    private fun populateNativeAd(nativeAd: NativeAd, adView: NativeAdView) {

        try {
            val mediaView: MediaView = adView.findViewById(R.id.ad_media)
            adView.mediaView = mediaView

            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)

            (adView.headlineView as TextView?)?.text = nativeAd.headline


            if (nativeAd.body == null) {
                adView.bodyView?.gone()
            } else {
                adView.bodyView?.visible()
                (adView.bodyView as TextView?)?.text = nativeAd.body
            }
            if (nativeAd.callToAction == null) {
                adView.callToActionView?.gone()
            } else {
                adView.callToActionView?.visible()
                (adView.callToActionView as Button?)?.text = nativeAd.callToAction
            }
            if (nativeAd.icon == null) {
                adView.iconView?.gone()
            } else {
                (adView.iconView as ImageView?)?.setImageDrawable(nativeAd.icon?.drawable)
                adView.iconView?.visible()
            }
            adView.setNativeAd(nativeAd)
        } catch (e: Exception) {
        }
    }


}