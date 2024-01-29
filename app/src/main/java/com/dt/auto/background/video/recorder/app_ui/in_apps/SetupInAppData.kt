package com.dt.auto.background.video.recorder.app_ui.in_apps

import android.content.Context
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.lifetimeProductId
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.monthlyProductId
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.oneWeekProductId
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.threeMonthsProductId


object SetupInAppData {

    fun setupProductIdes(context: Context) {

        try {

            threeMonthsProductId = context.getString(R.string.three_monthly_subscription)
            monthlyProductId = context.getString(R.string.monthly_subscription)
            oneWeekProductId = context.getString(R.string.one_week_subscription)
            lifetimeProductId = context.getString(R.string.lifetime)
        } catch (e: Exception) { }
    }

}