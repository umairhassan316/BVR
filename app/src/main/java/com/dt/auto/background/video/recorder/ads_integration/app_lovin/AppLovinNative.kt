package com.dt.auto.background.video.recorder.ads_integration.app_lovin

import android.os.Handler
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.facebook.shimmer.ShimmerFrameLayout
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.helpers.utils.invisible
import com.dt.auto.background.video.recorder.helpers.utils.log
import java.util.concurrent.TimeUnit
import kotlin.math.pow

object AppLovinNative {

    //    private val nativeAdContainerView: ViewGroup? = null
    var nativeAdLoader: MaxNativeAdLoader? = null
    var loadedNativeAd: MaxAd? = null

    fun showAppLovinNative(
        appCompatActivity: AppCompatActivity,
        nativeAdContainerView: FrameLayout,
        shimmer: ShimmerFrameLayout,
        loading: TextView,
    ) {

        loadAppLovinNative(appCompatActivity)


        nativeAdLoader = MaxNativeAdLoader(
            appCompatActivity.getString(R.string.native_app_lovin_id),
            appCompatActivity
        )
        nativeAdLoader?.setRevenueListener { }

        Log.d("ffnet", "native Applovin :showAppLovinNative")


        nativeAdLoader?.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, nativeAd: MaxAd?) {
                super.onNativeAdLoaded(nativeAdView, nativeAd)
                // Clean up any pre-existing native ad to prevent memory leaks.
                if (loadedNativeAd != null) {
                    nativeAdLoader?.destroy(loadedNativeAd)
                }

                shimmer.invisible()
                loading.invisible()
                Log.d("ffnet", "native Applovin :onNativeAdLoaded")
                log("onNativeAdLoaded dual space: ${nativeAd.toString()}")
                loadedNativeAd = nativeAd
                nativeAdContainerView.removeAllViews()
                nativeAdContainerView.addView(nativeAdView)
            }

            override fun onNativeAdLoadFailed(p0: String?, p1: MaxError?) {
                super.onNativeAdLoadFailed(p0, p1)
                Log.d("ffnet", "native Applovin :onNativeAdLoadFailed")
                log("onNativeAdLoadFailed: ${p1?.message.toString()}")
                loadAppLovinNative(appCompatActivity)

            }

            override fun onNativeAdClicked(p0: MaxAd?) {
                super.onNativeAdClicked(p0)
            }
        })
    }


    fun loadAppLovinNative(appCompatActivity: AppCompatActivity) {
        Log.d("ffnet", "native Applovin :createNativeAdView")

        nativeAdLoader?.loadAd(createNativeAdView(appCompatActivity))
    }


    private fun createNativeAdView(appCompatActivity: AppCompatActivity): MaxNativeAdView {
        Log.d("ffnet", "native Applovin :createNativeAdView")

        val binder: MaxNativeAdViewBinder =
            MaxNativeAdViewBinder.Builder(R.layout.app_lovin_native)
                .setTitleTextViewId(R.id.title_text_view)
                .setBodyTextViewId(R.id.body_text_view)
                .setAdvertiserTextViewId(R.id.advertiser_textView)
                .setIconImageViewId(R.id.icon_image_view)
                .setMediaContentViewGroupId(R.id.media_view_container)
//                .setOptionsContentViewGroupId(R.id.options_view)
                .setCallToActionButtonId(R.id.cta_button)
                .build()
        return MaxNativeAdView(binder, appCompatActivity)
    }



}