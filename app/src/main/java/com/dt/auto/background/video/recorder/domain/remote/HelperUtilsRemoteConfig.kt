package com.dt.auto.background.video.recorder.domain.remote


class HelperUtilsRemoteConfig {
    companion object {



        var startNative = 1 // ignore
        var startNativeEitherAdmobOrAppLovinInSplash = 1
        var startNativeEitherAdmobOrAppLovinInQualitySelection = 1
        var startNativeEitherAdmobOrAppLovinExitScreen = 1
        var startNativeEitherAdmobOrAppLovinInSettingsScreen = 1


        var startBanner = 1
        var startBannerEitherAdmobOrAppLovinInDashboard = 1
        var startBannerEitherAdmobOrAppLovinInVideosListScreen = 1


        var startInterstitial = 1
        var startInterstitialEitherAdmobOrAppLovinInSplash = 1
        var startInterstitialEitherAdmobOrAppLovinInDashboard = 1


        var appOpenAdId : String? = null
        var bannerAdId : String? = null
        var interstitialAdId : String? = null
        var nativeAdId : String? = null


    }
}