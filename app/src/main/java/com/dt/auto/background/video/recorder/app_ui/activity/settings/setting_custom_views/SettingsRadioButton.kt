package com.dt.auto.background.video.recorder.app_ui.activity.settings.setting_custom_views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.view.isInvisible
import com.dt.auto.background.video.recorder.R

class SettingsRadioButton : FrameLayout {
    private val view: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_settings_radio_button, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.SettingsRadioButton).apply {
            showText(this)
            showDelimiter(this)
            recycle()
        }

        view.setOnClickListener {
            findViewById<RadioButton>(R.id.radio_button).toggle()
        }
    }

    var isChecked: Boolean
        get() = view.findViewById<RadioButton>(R.id.radio_button).isChecked
        set(value) {
            view.findViewById<RadioButton>(R.id.radio_button).isChecked = value
        }

    fun setCheckedChangedListener(listener: ((Boolean) -> Unit)?) {
        view.findViewById<RadioButton>(R.id.radio_button)
            .setOnCheckedChangeListener { _, isChecked ->
                listener?.invoke(isChecked)
            }
    }

    private fun showText(attributes: TypedArray) {
        view.findViewById<TextView>(R.id.text_view_text).text =
            attributes.getString(R.styleable.SettingsRadioButton_text).orEmpty()
    }

    private fun showDelimiter(attributes: TypedArray) {
        view.findViewById<View>(R.id.delimiter).isInvisible =
            attributes.getBoolean(R.styleable.SettingsRadioButton_isDelimiterVisible, true).not()
    }
}