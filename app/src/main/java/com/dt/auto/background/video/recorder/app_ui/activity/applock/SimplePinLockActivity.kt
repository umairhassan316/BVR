package com.dt.auto.background.video.recorder.app_ui.activity.applock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.app_ui.activity.DashboardActivity
import com.dt.auto.background.video.recorder.base.BaseActivity
import com.dt.auto.background.video.recorder.databinding.ActivityPinLockBinding
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.helpers.utils.viewBinding
import net.aquarla.simplepinlock.Util

/*
 * Base class providing functionalities of PIN lock screen.
 */
open class SimplePinLockActivity : BaseActivity(), Util {

    private var pin = ""

    companion object {
        val CANCELLED = 1001
    }

    private var PIN_RESET=0
    private var PIN_ENTERY=0
    private var PIN_CHECK=0

    private val binding by viewBinding(ActivityPinLockBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        logEvent("SimplePinLockActivity")

        /*if (NetworkHelper.hasInternetConnection(this) && !BillingPurchases.isPurchase) {
            AdmobInterstitial.loadAdInterstitial(this@SimplePinLockActivity)
        }*/

        binding.gridView.adapter = PinButtonAdapter(this)
        binding.gridView.numColumns = 3

        checkPin()

        binding.cancelButton.setOnClickListener {
            onCancelButtonPressed()
        }

        binding.btnSubmit.setOnClickListener {
            if(PIN_CHECK==1){
                if(PIN_RESET==1){
                    settings.apply { isPinEnabled = true }
                    settings.apply { isPinSet = pin }
                    startActivity(Intent(this, DashboardActivity::class.java))
                }else {
                    if (settings.isPinSet?.equals(pin) == true) {
                        startActivity(Intent(this, DashboardActivity::class.java))

                       /* if (NetworkHelper.hasInternetConnection(this@SimplePinLockActivity)
                            && !BillingPurchases.isPurchase) {
                            if (AdCheckVariables.LOAD_ADMOB_INTER) {
                                AdmobInterstitial.showInterstitialAd(this@SimplePinLockActivity)
                            } else if (AdCheckVariables.LOAD_APPLOVIN_INTER) {
                                ApplovinInterstitials.showAd()
                            }
                        }*/
                    }else {
                        clearPin()
                        PIN_ENTERY++
                        binding.messageTextView.text = "Wrong Pin Enter Your Correct Pin"
                        if (PIN_ENTERY > 1) {
                            showDialog()
                        }
                    }
                }
            }else
                showDialog()
        }

        reloadPinView()

    }

    private fun checkPin() {
        PIN_CHECK=intent.getIntExtra("PIN_LOCK",0)

        if(PIN_CHECK==1){
            binding.messageTextView.text="Enter Your Pin"
        }
    }

    fun showDialog(){
        Log.d("JSON",pin)

        val alertDialog = AlertDialog.Builder(this, R.style.DialogTheme).create()

        val inflater: LayoutInflater = layoutInflater
        val view: View = inflater.inflate(R.layout.pin_con_dialogue, null)
        alertDialog.setView(view)

        val tv_title = view.findViewById<View>(R.id.tv_text) as TextView

        val et = view.findViewById<View>(R.id.etDialogue) as EditText

        val btncancel = view.findViewById<View>(R.id.btnCancel) as Button
        val btnDone = view.findViewById<View>(R.id.btnDone) as Button

        //multicolor textview text

        if(PIN_CHECK==1){
            tv_title.text="Enter Your NickName So you can reset Your Pin"
        }
        //multicolor textview text
        btncancel.setOnClickListener {
            try {
                alertDialog.dismiss()
            } catch (nu: NullPointerException) {
                nu.printStackTrace()
            }
        }

        btnDone.setOnClickListener(View.OnClickListener {
            try {
                if(et.text.toString().isEmpty()){
                    et.error="Enter Nickname"
                    et.requestFocus()
                }else {
                    if(PIN_CHECK==1){
                        if(settings.isNickname?.equals(et.text.toString().trim()) == true){
                            PIN_RESET=1
                            binding.messageTextView.text="Reset Your Pin"
                            binding.btnSubmit.text="Reset"
                            alertDialog.dismiss()
                        }
                        else{
                            et.error="Invalid Nickname"
                            et.requestFocus()
                        }
                    }else {
                        settings.apply { isPinEnabled = true }
                        settings.apply { isPinSet = pin }
                        settings.apply { isNickname = et.text.toString() }
                        alertDialog.dismiss()
                        finish()
                    }
                }
            } catch (nu: NullPointerException) {
                nu.printStackTrace()
            }
        })

        alertDialog.show()

    }

    fun reloadPinView() {

        val dotSize = dp2px(this, 20.0f).toInt()
        val dotMargin = dp2px(this, 10.0f).toInt()
        binding.pinView.removeAllViews()
        (1..getMaxPinSize()).forEach {
            val imageView = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(dotSize, dotSize, 0.0f)
            layoutParams.setMargins(dotMargin, dotMargin, dotMargin, dotMargin)
            imageView.layoutParams = layoutParams
            if (it > pin.length) {
                imageView.setImageResource(R.drawable.dot_fill)
            } else {
                imageView.setImageResource(R.drawable.dot_empty)
            }
            binding.pinView.addView(imageView)
        }
    }

    open fun getMaxPinSize() : Int {
        return 4
    }

    open fun onPinButtonClicked(text: String) {
        if (pin.length < getMaxPinSize()) {
            this.pin += text
            reloadPinView()
        }

        if (pin.length == getMaxPinSize()) {
            //onPinInputFinished()
            binding.btnSubmit.visibility=View.VISIBLE
        }
    }

    /*
     * Delete one character.
     */
    open fun onDeleteButtonClicked() {
        if (pin.length > 0) {
            pin = pin.substring(0, pin.length - 1)
            reloadPinView()
        }
    }

    fun getPin() : String {
        return pin
    }

    fun clearPin() {
        pin = ""
        reloadPinView()
    }


    //abstract fun onPinInputFinished()

    /*
     * Called when "CANCEL" button is pressed.
     */
    open fun onCancelButtonPressed() : Boolean {
        setResult(CANCELLED)
        finish()
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (pin.length != 0) {
                onDeleteButtonClicked()
            } else {
                setResult(CANCELLED)
                finish()
            }
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }
}
