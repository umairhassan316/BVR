package com.dt.auto.background.video.recorder.ads_integration.ad_show_dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.ads_integration.ad_show_dialog.AdDelayDialog.dialog

object AdDelayDialog {
    @JvmStatic
    var dialog: Dialog? = null
}

object AdDelayDialogTwo {

    @JvmStatic
    fun showDelayDialogForJava(appCompatActivity: AppCompatActivity) {
        dialog = Dialog(appCompatActivity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.layout_ad_delay_progress_dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
    }

    @JvmStatic
    fun dismissDialogForJava() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

}