package com.dt.auto.background.video.recorder.ads_integration.app_lovin

import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdkUtils
import com.facebook.shimmer.ShimmerFrameLayout
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.helpers.utils.invisible


object AppLovinBanner {


    fun loadAndShowAppLovinBanner(context: AppCompatActivity, view: FrameLayout, shimmer: ShimmerFrameLayout) {
        val adView = MaxAdView(context.getString(R.string.app_lovin_banner_id), context)

        adView.setListener(object : MaxAdViewAdListener {
            override fun onAdLoaded(ad: MaxAd?) {
                Log.d("ffnet", "Loaded max banner")
                shimmer.invisible()
            }

            override fun onAdDisplayed(ad: MaxAd?) {
            }

            override fun onAdHidden(ad: MaxAd?) {
            }

            override fun onAdClicked(ad: MaxAd?) {
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                Log.d("ffnet", "Failed Max onAdLoadFailed banner ${error?.message}")

            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                Log.d("ffnet", "Failed Display Max onAdDisplayFailed baner ${error?.message}")

            }

            override fun onAdExpanded(ad: MaxAd?) {
            }

            override fun onAdCollapsed(ad: MaxAd?) {
            }
        })
        adView.setRevenueListener { ad ->

            val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX)
            adjustAdRevenue.setRevenue(ad?.revenue, "USD")
            adjustAdRevenue.setAdRevenueNetwork(ad?.networkName)
            adjustAdRevenue.setAdRevenueUnit(ad?.adUnitId)
            adjustAdRevenue.setAdRevenuePlacement(ad?.placement)

            Adjust.trackAdRevenue(adjustAdRevenue)
        }


//        val isTablet = AppLovinSdkUtils.isTablet(context)
//        val heightPx = AppLovinSdkUtils.dpToPx(context, if (isTablet) 90 else 50)
//
//        adView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx)
//        adView.setBackgroundColor(Color.WHITE)
        view.addView(adView)
//        val rootView = view.findViewById<ViewGroup>(android.R.id.content)
        adView.loadAd()

    }}