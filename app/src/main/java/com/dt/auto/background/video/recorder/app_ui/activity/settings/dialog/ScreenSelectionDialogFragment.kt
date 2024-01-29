package com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.app_ui.activity.settings.setting_custom_views.SettingsRadioButton

class ScreenSelectionDialogFragment : DialogFragment() {

    companion object {

        fun newInstance(): ScreenSelectionDialogFragment {
            return ScreenSelectionDialogFragment().apply {
                isCancelable = false
            }
        }
    }

    interface Listener {
        fun onCancelScreenSelectionConfirmed()
        fun onScreenSelection()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = requireActivity() as? Listener ?: parentFragment as? Listener
        val view = layoutInflater.inflate(R.layout.screen_selection_dialog, null, false)

        val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setTitle(getString(R.string.preview_size))
            .setView(view)
            .setPositiveButton(R.string.cancel_btn) { _, _ -> listener?.onCancelScreenSelectionConfirmed() }
            .create()

        settings.apply {
            if (previewSize?.equals(getString(R.string.medium)) == true) {
                view.findViewById<SettingsRadioButton>(R.id.medium_button)?.isChecked = true
                view.findViewById<SettingsRadioButton>(R.id.small_button)?.isChecked = false
                view.findViewById<SettingsRadioButton>(R.id.large_button)?.isChecked = false
            } else if (previewSize?.equals(getString(R.string.small)) == true) {
                view.findViewById<SettingsRadioButton>(R.id.medium_button)?.isChecked = false
                view.findViewById<SettingsRadioButton>(R.id.small_button)?.isChecked = true
                view.findViewById<SettingsRadioButton>(R.id.large_button)?.isChecked = false
            } else if (previewSize?.equals(getString(R.string.large)) == true) {
                view.findViewById<SettingsRadioButton>(R.id.medium_button)?.isChecked = false
                view.findViewById<SettingsRadioButton>(R.id.small_button)?.isChecked = false
                view.findViewById<SettingsRadioButton>(R.id.large_button)?.isChecked = true
            }

        }
        view.findViewById<SettingsRadioButton>(R.id.small_button)?.setCheckedChangedListener {
            view.findViewById<SettingsRadioButton>(R.id.small_button)?.isChecked = it
            view.findViewById<SettingsRadioButton>(R.id.medium_button)?.isChecked = false
            view.findViewById<SettingsRadioButton>(R.id.large_button)?.isChecked = false
            settings.apply { previewSize = requireActivity().getString(R.string.small) }
            listener?.onScreenSelection()
            dialog.dismiss()
        }

        view.findViewById<SettingsRadioButton>(R.id.medium_button)?.setCheckedChangedListener {
            view.findViewById<SettingsRadioButton>(R.id.small_button)?.isChecked = false
            view.findViewById<SettingsRadioButton>(R.id.medium_button)?.isChecked = it
            view.findViewById<SettingsRadioButton>(R.id.large_button)?.isChecked = false
            settings.apply { previewSize = requireActivity().getString(R.string.medium) }
            listener?.onScreenSelection()
            dialog.dismiss()

        }
        view.findViewById<SettingsRadioButton>(R.id.large_button)?.setCheckedChangedListener {
            view.findViewById<SettingsRadioButton>(R.id.small_button)?.isChecked = false
            view.findViewById<SettingsRadioButton>(R.id.medium_button)?.isChecked = false
            view.findViewById<SettingsRadioButton>(R.id.large_button)?.isChecked = it
            settings.apply { previewSize = requireActivity().getString(R.string.large) }
            listener?.onScreenSelection()
            dialog.dismiss()

        }

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        return dialog
    }
}