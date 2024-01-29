package com.dt.auto.background.video.recorder.ads_integration.admob;

public interface AdsResponse {
    public void isApplovinBannerShow(boolean showAds);
    public void isApplovinInterstitialShow(boolean showAds);
    public void isApplovinNativeShow(boolean showAds);

    public void isFacebookNativeShow(boolean showAds);
    public void isFacebookBannerShow(boolean showAds);
}
