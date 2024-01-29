package com.dt.auto.background.video.recorder.base

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.ads_integration.admob.AdmobInterstitial
import com.dt.auto.background.video.recorder.ads_integration.admob.AdmobNativeImplementation
import com.dt.auto.background.video.recorder.ads_integration.admob.AdsResponse
import com.dt.auto.background.video.recorder.ads_integration.admob.AppOpenManager
import com.dt.auto.background.video.recorder.ads_integration.admob.BannersImplementation.showBannerAd
import com.dt.auto.background.video.recorder.ads_integration.app_lovin.AppLovinBanner.loadAndShowAppLovinBanner
import com.dt.auto.background.video.recorder.ads_integration.app_lovin.AppLovinNative
import com.dt.auto.background.video.recorder.ads_integration.app_lovin.ApplovinInterstitials
import com.dt.auto.background.video.recorder.ads_integration.facebook.FacebookBanner
import com.dt.auto.background.video.recorder.ads_integration.facebook.FacebookNative
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.isPurchase
import com.dt.auto.background.video.recorder.application.RecorderApplication.Companion.mFirebaseAnalytics
import com.dt.auto.background.video.recorder.helpers.NetworkHelper.hasInternetConnection
import com.dt.auto.background.video.recorder.helpers.utils.gone
import com.dt.auto.background.video.recorder.helpers.utils.visible

open class BaseActivity : AppCompatActivity(), AdsResponse ,AppOpenManager.Listener{

    //    private val nativeAdContainerView: ViewGroup? = null
    var nativeAdLoader: MaxNativeAdLoader? = null
    var loadedNativeAd: MaxAd? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    open fun logEvent(name: String) {
        val params = Bundle()
        params.putString(name, name)
        mFirebaseAnalytics?.logEvent(name, params)
    }

    fun showBanner() {
        if (hasInternetConnection(this) && !isPurchase) {
            showBannerAd(
                this@BaseActivity,
                findViewById(R.id.banner_container),
                findViewById(R.id.shimmer_view_container),
                1,this)
        } else {
            findViewById<View>(R.id.banner_container).gone()
        }
    }


    fun showNativeAds() { //call on other screen when admob failed call facebook ads
        if (hasInternetConnection(this) && !isPurchase) {
//            AppLovinNative.loadAppLovinNative(this@BaseActivity)
//
            AdmobNativeImplementation.showNativeAdmob(
                findViewById(R.id.loading_tv),
                findViewById(R.id.native_container),
                findViewById(R.id.shimmer_view_container),
                this@BaseActivity,
                this
            )
        } else {
            findViewById<View>(R.id.native_container_holder).gone()

        }

    }


    fun showInterstitial() {
        if (hasInternetConnection(this) && !isPurchase) {
            AdmobInterstitial.showInterstitialAd(this@BaseActivity)

        } else {
            findViewById<View>(R.id.native_container_holder).gone()

        }
    }

    fun showInterstitialSplash() {
        if (hasInternetConnection(this) && !isPurchase) {
            AdmobInterstitial.showInterstitialAd(this@BaseActivity)

        } else {
            findViewById<View>(R.id.native_container_holder).gone()

        }
    }


    fun hideNativeAd(){
        findViewById<View>(R.id.native_container_holder).gone()
    }

    fun visibleNativeAd(){
        if (hasInternetConnection(this) && !isPurchase) {
            findViewById<View>(R.id.native_container_holder).visible()
        }
    }


    override fun isApplovinBannerShow(showAds: Boolean) {
        if(!showAds){
            loadAndShowAppLovinBanner(
                this@BaseActivity,
                findViewById(R.id.banner_container),
                findViewById(R.id.shimmer_view_container)
            )
        }
    }

    override fun isApplovinInterstitialShow(showAds: Boolean) {
        if(!showAds) {
            ApplovinInterstitials.loadApplovinInterstitial(this@BaseActivity)
        }
    }

    override fun isApplovinNativeShow(showAds: Boolean) {

        if(!showAds){

            Log.d("ffnet", "isApplovinNativeShow "+this@BaseActivity)
            AppLovinNative.showAppLovinNative(
                this@BaseActivity,
                findViewById(R.id.native_container),
                findViewById(R.id.shimmer_view_container),
                findViewById(R.id.loading_tv)
            )

            AppLovinNative.showAppLovinNative(
                this@BaseActivity,
                findViewById(R.id.native_container),
                findViewById(R.id.shimmer_view_container),
                findViewById(R.id.loading_tv)
            )
        }
    }

    override fun isFacebookNativeShow(showAds: Boolean) {
        if(!showAds){
            FacebookNative.load_Native_Fb(
                findViewById(R.id.loading_tv),
                findViewById(R.id.native_container),
                findViewById(R.id.shimmer_view_container),
                this@BaseActivity,
                this)
        }
    }

    override fun isFacebookBannerShow(showAds: Boolean) {
        if(!showAds){
            FacebookBanner.showBannerAd(
                this@BaseActivity,
                findViewById(R.id.banner_container),
                findViewById(R.id.shimmer_view_container),
                1,this)
        }
    }

    override fun onNativeAdClosed() {
        showNativeAds()
    }


}

