package com.dt.auto.background.video.recorder.app_ui.activity.settings.setting_custom_views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.dt.auto.background.video.recorder.R

class SettingsButton : FrameLayout {
    private val view: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_settings_button, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.SettingsButton).apply {
            showText(this)
            showHint(this)
            showSwitch(this)
            recycle()
        }
    }
    
    var hint: String
        get() = view.findViewById<TextView>(R.id.text_view_hint).text.toString()
        set(value) {
            view.findViewById<TextView>(R.id.text_view_hint).apply {
                text = value
                isVisible = text.isNullOrEmpty().not()
            }
        }

    var isChecked: Boolean
        get() = view.findViewById<SwitchCompat>(R.id.switch_button).isChecked
        set(value) { view.findViewById<SwitchCompat>(R.id.switch_button).isChecked = value }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        view.findViewById<TextView>(R.id.text_view_text).isEnabled = enabled
    }

    fun setCheckedChangedListener(listener: ((Boolean) -> Unit)?) {
        view.findViewById<SwitchCompat>(R.id.switch_button).setOnCheckedChangeListener { _, isChecked ->
            listener?.invoke(isChecked)
        }
    }

    private fun showText(attributes: TypedArray) {
        view.findViewById<TextView>(R.id.text_view_text).text = attributes.getString(R.styleable.SettingsButton_text).orEmpty()
    }

    private fun showHint(attributes: TypedArray) {
        hint = attributes.getString(R.styleable.SettingsButton_hint).orEmpty()
    }

    private fun showSwitch(attributes: TypedArray) {
        view.findViewById<SwitchCompat>(R.id.switch_button).isVisible = attributes.getBoolean(R.styleable.SettingsButton_isSwitchVisible, true)
        if (view.findViewById<SwitchCompat>(R.id.switch_button).isVisible) {
            view.setOnClickListener {
                view.findViewById<SwitchCompat>(R.id.switch_button).toggle()
            }
        }
    }
}