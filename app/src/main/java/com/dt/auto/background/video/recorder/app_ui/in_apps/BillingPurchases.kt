package com.dt.auto.background.video.recorder.app_ui.in_apps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.dt.auto.background.video.recorder.app_ui.in_apps.SetupInAppData.setupProductIdes
import com.dt.auto.background.video.recorder.app_ui.activity.settings.use_case.Settings


@SuppressLint("StaticFieldLeak")
object BillingPurchases {


    @JvmField
    var threeMonthsPrice: String? = null
    @JvmField
    var monthlyPrice: String? = null
    @JvmField
    var oneWeekPrice: String? = null

    @JvmField
    var lifetimeprice: String? = null

    var billingClient: BillingClient? = null


    var threeMonthsProductId: String? = null
    var monthlyProductId: String? = null
    var oneWeekProductId: String? = null
    var lifetimeProductId: String? = null

    @JvmField
    var isPurchase = false

    @JvmField
    var isBpClientReady = false
    var contextGlobal: Context? = null

    @JvmField
    var threeMonthsSkuDetails: SkuDetails? = null
    @JvmField
    var monthlySkuDetails: SkuDetails? = null
    @JvmField
    var oneWeekSkuDetails: SkuDetails? = null

    @JvmField
    var lifetimeSkuDetails: SkuDetails? = null

    var purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, list ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {

            for (purchase in list) {
                handlePurchase(purchase, contextGlobal)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED && list != null) {
            Toast.makeText(contextGlobal, "Item Already Owned", Toast.LENGTH_SHORT).show()

            for (purchase in list) {
                handlePurchase(purchase, contextGlobal)
            }

        } else {
            try {
                if (billingResult.debugMessage.isNotEmpty()) {
                    Toast.makeText(contextGlobal, billingResult.debugMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    var billingClientStateListener: BillingClientStateListener =
        object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d("ffnet", "onBillingSetupFinished::  ${billingResult.responseCode}")

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("ffnet", "onBillingSetupFinished::  success")

                    isBpClientReady = true
                    try {
                        getOwnedPurchases()
                        querySubscriptions()
                        queryProducts()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

            override fun onBillingServiceDisconnected() {}
        }

    fun setupBillingClient(context: Context) {
        contextGlobal = context

        setupProductIdes(context)


        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        if (billingClient?.isReady == false) {
            billingClient?.startConnection(billingClientStateListener)
        }
    }

    @JvmStatic
    fun launchBillingFlow(activity: Activity, skuDetails: SkuDetails) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        val responseCode =
            billingClient?.launchBillingFlow(activity, billingFlowParams)?.responseCode

        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                Toast.makeText(activity, "Billing Unavailable!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun querySubscriptions() {
        val skuListInApp: MutableList<String?> = ArrayList()
        try {
            skuListInApp.add(monthlyProductId)
            skuListInApp.add(threeMonthsProductId)
            skuListInApp.add(oneWeekProductId)
        } catch (e: Exception) {
            Log.d("ffnet", "querySubscriptions:: Error inAppPurchases ${e.toString()}")
        }

        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuListInApp).setType(BillingClient.SkuType.SUBS)
        billingClient?.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList -> // Process the result.

            try {

                if (skuDetailsList!!.size > 0) {
                    for (skuDetails in skuDetailsList) {

                        when (skuDetails.sku) {

                            monthlyProductId -> {
                                monthlySkuDetails = skuDetails
                                monthlyPrice = skuDetails.price
                            }


                            threeMonthsProductId -> {
                                threeMonthsSkuDetails = skuDetails
                                threeMonthsPrice = skuDetails.price
                            }


                            oneWeekProductId -> {
                                oneWeekSkuDetails = skuDetails
                                oneWeekPrice = skuDetails.price
                            }





                        }
                    }
                } else {

                    monthlySkuDetails = null
                    threeMonthsSkuDetails= null
                    oneWeekSkuDetails = null


                }
            } catch (e: Exception) {
                Log.d("ffnet", "querySubscriptions:: Error skuDetailsList.size ${e.toString()}")

            }
        }
    }

    fun queryProducts() {
        val skuListInApp: MutableList<String?> = ArrayList()
        skuListInApp.add(lifetimeProductId)

        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuListInApp).setType(BillingClient.SkuType.INAPP)
        billingClient!!.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList ->
            try {
                if (skuDetailsList!!.size > 0) {
                    for (skuDetails in skuDetailsList) {
                        when (skuDetails.sku) {

                            lifetimeProductId -> {
                                lifetimeSkuDetails = skuDetails
                                lifetimeprice = skuDetails.price

                            }
                        }
                    }
                } else {

                    lifetimeSkuDetails = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun getOwnedPurchases() {
        /*
        * Subscriptions
        * */
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, mutableList ->
            Log.d("ffnet", "getOwnedPurchases:  subs prod id list $mutableList")


//            if (!isPurchase) {
            mutableList.map { purchase ->
                Log.d("ffnet", "getOwnedPurchases:  subs prod id checks ${purchase}")

                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    Log.d("ffnet", "check:  if block subscription State ${purchase.purchaseState}")
                    purchase.products.map {
                    }
                    isPurchase = true
                    Settings.getInstance(contextGlobal!!).is_Purchased.apply { true }
                } else {
                    Log.d(
                        "ffnet",
                        "check:  else block subscription State ${purchase.purchaseState}"
                    )
                    isPurchase = false

                }


            }

        }

        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, mutableList ->
            Log.d("ffnet", "getOwnedPurchases:  INAPP prod id list $mutableList")


//            if (!isPurchase) {
            mutableList.map { purchase ->
                Log.d("ffnet", "getOwnedPurchases:  INAPP prod id checks ${purchase}")

                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    Log.d("ffnet", "check:  if block INAPP State ${purchase.purchaseState}")
                    purchase.products.map {
                    }
                    isPurchase = true
                    Settings.getInstance(contextGlobal!!).is_Purchased.apply { true }
                } else {
                    Log.d(
                        "ffnet",
                        "check:  else block INAPP State ${purchase.purchaseState}"
                    )
                    isPurchase = false

                }


            }

        }

    }

    fun onPurchasedItemDialog(context: Context?) {
        getOwnedPurchases()
    }

    fun handlePurchase(purchase: Purchase, context: Context?) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                val acknowledgePurchaseResponseListener =
                    AcknowledgePurchaseResponseListener { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            onPurchasedItemDialog(context)
                        }
                    }
                billingClient?.acknowledgePurchase(
                    acknowledgePurchaseParams,
                    acknowledgePurchaseResponseListener
                )
            } else {
                onPurchasedItemDialog(context)
            }
        }
    }
}