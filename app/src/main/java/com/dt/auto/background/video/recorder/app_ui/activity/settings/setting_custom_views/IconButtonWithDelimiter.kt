package com.dt.auto.background.video.recorder.app_ui.activity.settings.setting_custom_views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.dt.auto.background.video.recorder.R


class IconButtonWithDelimiter : FrameLayout {
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
            .inflate(R.layout.layout_icon_button_with_delimiter, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.IconButtonWithDelimiter).apply {
            showIcon(this)
            showIconBackgroundColor(this)
            showText(this)
            showArrow(this)
            showDelimiter(this)
            recycle()
        }
    }

    private fun showIcon(attributes: TypedArray) {
        val iconResId = attributes.getResourceId(R.styleable.IconButtonWithDelimiter_icon, -1)
        val icon = AppCompatResources.getDrawable(context, iconResId)
        view.findViewById<ImageView>(R.id.image_view_schema).setImageDrawable(icon)
    }

    private fun showIconBackgroundColor(attributes: TypedArray) {
        val color = attributes.getColor(
            R.styleable.IconButtonWithDelimiter_iconBackground,
            view.context.resources.getColor(R.color.green)
        )
        ((view.findViewById<FrameLayout>(R.id.layout_image)).background.mutate() as GradientDrawable).setColor(
            color
        )
    }

    private fun showText(attributes: TypedArray) {
        view.findViewById<TextView>(R.id.text_view).text =
            attributes.getString(R.styleable.IconButtonWithDelimiter_text).orEmpty()
    }

    private fun showArrow(attributes: TypedArray) {
        view.findViewById<ImageView>(R.id.image_view_arrow).isVisible =
            attributes.getBoolean(R.styleable.IconButtonWithDelimiter_isArrowVisible, false)
    }

    private fun showDelimiter(attributes: TypedArray) {
        (view.findViewById<View>(R.id.delimiter)).isInvisible =
            attributes.getBoolean(R.styleable.IconButtonWithDelimiter_isDelimiterVisible, true)
                .not()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        view.findViewById<ImageView>(R.id.image_view_schema).isEnabled = enabled
        view.findViewById<TextView>(R.id.text_view).isEnabled = enabled
    }
}