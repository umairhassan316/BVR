package com.dt.auto.background.video.recorder.helpers.utils


import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.ads_integration.ad_show_dialog.AdDelayDialog.dialog
import com.dt.auto.background.video.recorder.helpers.VersionCheckHelper.isOreoPlus



fun Context.toast(msg: Int, len: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, len).show()
}

fun Context.toastInfo(msg: String, len: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, len).show()
}

fun Context.toastError(msg: Int, len: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, len).show()
}

fun Context.toastWarning(msg: Int, len: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, len).show()
}


fun log(msg: String) {
    Log.d("LOG_BVR","Message "+msg)
}


fun AppCompatActivity.setupFullScreen() {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
}

fun AppCompatActivity.showDelayDialog() {
    dialog = Dialog(this)
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.setCancelable(false)
    dialog?.setContentView(R.layout.layout_ad_delay_progress_dialog)
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog?.show()
}

fun dismissDelayDialog() {
    if (dialog?.isShowing == true) {
        dialog?.dismiss()
    }
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}


fun View.inflate(resId: Int, container: ViewGroup, attach: Boolean): View {
    return LayoutInflater.from(this.context).inflate(resId, container, attach)
}

fun Int.dp(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
}


internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) =
    AppCompatResources.getDrawable(this, drawable)

internal fun ImageView.setBackgroundDrawable(@DrawableRes drawable: Int) =
    setImageDrawable(context.getDrawableCompat(drawable))


internal fun View.setBackgroundTintCompat(@ColorRes color: Int) = ViewCompat.setBackgroundTintList(
    this,
    ContextCompat.getColorStateList(context, color)
)


internal fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}

fun Context.makeRestart(clazz: AppCompatActivity) {
    startActivity(Intent.makeRestartActivityTask(clazz.getComponentName()))
}

internal fun View.hideSoftInput() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}


inline fun View.setOnClickListenerCoolDown(
    coolDown: Long = 1000L,
    crossinline action: (view: View) -> Unit
) {
    setOnClickListener(object : View.OnClickListener {
        var lastTime = 0L
        override fun onClick(v: View) {
            val now = System.currentTimeMillis()
            if (now - lastTime > coolDown) {
                action(v)
                lastTime = now
            }
        }
    })
}

var View.elevationCompat: Float
    get() {
        return elevation
    }
    set(value) {
        elevation = value
    }

fun RecyclerView.smoothScrollToPos(position: Int) {
    Handler(Looper.getMainLooper()).postDelayed({
        this.smoothScrollToPosition(position)
    }, 300)
}


fun <T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.updateList(list: List<T>?) {
    this.submitList(
        if (list == this.currentList) {
            log("Same list")
            list.toList()
        } else {
            log("Not Same list")
            list
        }
    )
}

fun <T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.addRestorePolicy() {
    stateRestorationPolicy =
        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
}


fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

/*
* https://stackoverflow.com/questions/3053761/reload-activity-in-android
* */
fun AppCompatActivity.reset() {
    overridePendingTransition(0, 0)
    finish()
    overridePendingTransition(0, 0)
    startActivity(intent)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
    overridePendingTransition(0, 0)
}

fun AppCompatActivity.vibrateOnClick() {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        if (isOreoPlus()) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }
}
