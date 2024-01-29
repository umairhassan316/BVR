//package com.dt.auto.background.video.recorder.domain.remote
//
//import android.content.Context
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
//import com.dt.auto.background.video.recorder.BuildConfig
//import com.dt.auto.background.video.recorder.R
//import com.dt.auto.background.video.recorder.ads_integration.admob.AdmobInterstitial.loadAdInterstitial
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.appOpenAdId
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.bannerAdId
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.interstitialAdId
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.nativeAdId
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.startBannerEitherAdmobOrAppLovinInDashboard
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.startBannerEitherAdmobOrAppLovinInVideosListScreen
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.startInterstitialEitherAdmobOrAppLovinInDashboard
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.startInterstitialEitherAdmobOrAppLovinInSplash
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.startNativeEitherAdmobOrAppLovinExitScreen
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.startNativeEitherAdmobOrAppLovinInQualitySelection
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.startNativeEitherAdmobOrAppLovinInSettingsScreen
//import com.dt.auto.background.video.recorder.domain.remote.HelperUtilsRemoteConfig.Companion.startNativeEitherAdmobOrAppLovinInSplash
//import com.dt.auto.background.video.recorder.helpers.utils.log
//import org.json.JSONObject
//
//
///*
//* Todo: RemoteConfigurationClass:: This Class will handle all the remote configuration tasks
//* */
//object RemoteConfigurationClass {
//
//    private val mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
//
//
//    fun setupRemoteConfiguration(mContext: Context) {
//
//        if (!BuildConfig.DEBUG) {
//            /*
//            * Release Build
//            * */
//            val configBuilder = FirebaseRemoteConfigSettings.Builder()
//
//            val cacheInterval: Long = 0
//            configBuilder.minimumFetchIntervalInSeconds = cacheInterval
//            mFirebaseRemoteConfig.setConfigSettingsAsync(configBuilder.build())
//            mFirebaseRemoteConfig.fetchAndActivate()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        mFirebaseRemoteConfig.activate()
//
//
//                        try {
//                            val bvrJson = mFirebaseRemoteConfig.getString("bvrJson")
//
//                            val jsonObjectDualSpace = JSONObject(bvrJson)
//
//                            try {
//                                interstitialAdId = jsonObjectDualSpace.getString("interstitialAdId")
//                                log("interstitialAdId " + jsonObjectDualSpace.getString("interstitialAdId"))
//                            } catch (e: Exception) {
//                            }
//
//
//                            try {
//                                startInterstitialEitherAdmobOrAppLovinInSplash =
//                                    jsonObjectDualSpace.getInt("startInterstitialEitherAdmobOrAppLovinInSplash")
//                                log(
//                                    "startInterstitialEitherAdmobOrAppLovinInSplash: " + jsonObjectDualSpace.getInt(
//                                        "startInterstitialEitherAdmobOrAppLovinInSplash"
//                                    )
//                                )
//                            } catch (e: Exception) {
//                                startInterstitialEitherAdmobOrAppLovinInSplash = 1
//                            }
//
//                            try {
//                                startInterstitialEitherAdmobOrAppLovinInDashboard =
//                                    jsonObjectDualSpace.getInt("startInterstitialEitherAdmobOrAppLovinInDashboard")
//                                log(
//                                    "startInterstitialEitherAdmobOrAppLovinInDashboard: " + jsonObjectDualSpace.getInt(
//                                        "startInterstitialEitherAdmobOrAppLovinInDashboard"
//                                    )
//                                )
//                            } catch (e: Exception) {
//                                startInterstitialEitherAdmobOrAppLovinInDashboard = 1
//                            }
//
//                            try {
//                                startNativeEitherAdmobOrAppLovinInSplash =
//                                    jsonObjectDualSpace.getInt("startNativeEitherAdmobOrAppLovinInSplash")
//                                log(
//                                    "startNativeEitherAdmobOrAppLovinInSplash: ${
//                                        jsonObjectDualSpace.getInt(
//                                            "startNativeEitherAdmobOrAppLovinInSplash"
//                                        )
//                                    }"
//                                )
//                            } catch (e: Exception) {
//                                startNativeEitherAdmobOrAppLovinInSplash = 1
//                            }
//                            try {
//                                startNativeEitherAdmobOrAppLovinInQualitySelection =
//                                    jsonObjectDualSpace.getInt("startNativeEitherAdmobOrAppLovinInQualitySelection")
//                                log(
//                                    "startNativeEitherAdmobOrAppLovinInQualitySelection: ${
//                                        jsonObjectDualSpace.getInt(
//                                            "startNativeEitherAdmobOrAppLovinInQualitySelection"
//                                        )
//                                    }"
//                                )
//                            } catch (e: Exception) {
//                                startNativeEitherAdmobOrAppLovinInQualitySelection = 1
//                            }
//                            try {
//                                startNativeEitherAdmobOrAppLovinExitScreen =
//                                    jsonObjectDualSpace.getInt("startNativeEitherAdmobOrAppLovinExitScreen")
//                                log(
//                                    "startNativeEitherAdmobOrAppLovinExitScreen: ${
//                                        jsonObjectDualSpace.getInt(
//                                            "startNativeEitherAdmobOrAppLovinExitScreen"
//                                        )
//                                    }"
//                                )
//                            } catch (e: Exception) {
//                                startNativeEitherAdmobOrAppLovinExitScreen = 1
//                            }
//                            try {
//                                startNativeEitherAdmobOrAppLovinInSettingsScreen =
//                                    jsonObjectDualSpace.getInt("startNativeEitherAdmobOrAppLovinInSettingsScreen")
//                                log(
//                                    "startNativeEitherAdmobOrAppLovinInSettingsScreen: ${
//                                        jsonObjectDualSpace.getInt(
//                                            "startNativeEitherAdmobOrAppLovinInSettingsScreen"
//                                        )
//                                    }"
//                                )
//                            } catch (e: Exception) {
//                                startNativeEitherAdmobOrAppLovinInSettingsScreen = 1
//                            }
//
//
//
//                            try {
//                                startBannerEitherAdmobOrAppLovinInDashboard =
//                                    jsonObjectDualSpace.getInt("startBannerEitherAdmobOrAppLovinInDashboard")
//                                log(
//                                    "startBannerEitherAdmobOrAppLovinInDashboard: " + jsonObjectDualSpace.getInt(
//                                        "startBannerEitherAdmobOrAppLovinInDashboard"
//                                    )
//                                )
//                            } catch (e: Exception) {
//                                startBannerEitherAdmobOrAppLovinInDashboard = 1
//                            }
//                            try {
//                                startBannerEitherAdmobOrAppLovinInVideosListScreen =
//                                    jsonObjectDualSpace.getInt("startBannerEitherAdmobOrAppLovinInVideosListScreen")
//                                log(
//                                    "startBannerEitherAdmobOrAppLovinInVideosListScreen: " + jsonObjectDualSpace.getInt(
//                                        "startBannerEitherAdmobOrAppLovinInVideosListScreen"
//                                    )
//                                )
//                            } catch (e: Exception) {
//                                startBannerEitherAdmobOrAppLovinInVideosListScreen = 1
//                            }
//
//
//                            try {
//                                appOpenAdId = jsonObjectDualSpace.getString("appOpenAdId")
//                                log("appOpenAdId " + jsonObjectDualSpace.getString("appOpenAdId"))
//                            } catch (e: Exception) {
//                            }
//
//                            try {
//                                bannerAdId = jsonObjectDualSpace.getString("bannerAdId")
//                                log("bannerAdId " + jsonObjectDualSpace.getString("bannerAdId"))
//                            } catch (e: Exception) {
//                            }
//
//
//
//                            try {
//                                nativeAdId = jsonObjectDualSpace.getString("nativeAdId")
//                                log("nativeAdId " + jsonObjectDualSpace.getString("nativeAdId"))
//                            } catch (e: Exception) {
//                            }
//
//
//
//                            try {
//                                loadAdInterstitial(mContext)
//                            } catch (e: Exception) {
//                            }
//                        } catch (e: Exception) {
//                            log("error from json object: $e")
//                        }
//
//                    }
//                }
//        } else {
//
//            var startNative = 1
//            var startBanner = 1
//            var startInterstitial = 1
//
//            var startNativeEitherAdmobOrAppLovinInSplash = 1
//            var startNativeEitherAdmobOrAppLovinInQualitySelection = 1
//            var startNativeEitherAdmobOrAppLovinExitScreen = 1
//            var startNativeEitherAdmobOrAppLovinInSettingsScreen = 1
//
//            var EitherAdmobOrAppLovinInDashboard = 1
//            var EitherAdmobOrAppLovinInVideosListScreen = 1
//
//            var startInterstitialEitherAdmobOrAppLovinInSplash = 1
//            var startInterstitialEitherAdmobOrAppLovinInDashboard = 1
//
//
//            interstitialAdId = mContext.getString(R.string.interstitial_id)
//            nativeAdId = mContext.getString(R.string.native_id)
//            bannerAdId = mContext.getString(R.string.banner_id)
//            appOpenAdId = mContext.getString(R.string.app_open_id)
//
//            try {
//                loadAdInterstitial(mContext)
//            } catch (e: Exception) {
//            }
//        }
//    }
//
//
//}