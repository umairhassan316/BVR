package com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.app_ui.activity.settings.setting_custom_views.SettingsRadioButton

class CameraSelectionDialogFragment : DialogFragment() {

    companion object {

        fun newInstance(): CameraSelectionDialogFragment {
            return CameraSelectionDialogFragment().apply {
                isCancelable = false
            }
        }
    }

    interface Listener {
        fun onCancelConfirmed()
        fun OnCameraSelection()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = requireActivity() as? Listener ?: parentFragment as? Listener
        val view = layoutInflater.inflate(R.layout.select_camera_dialog, null, false)

        val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setTitle(getString(R.string.camera_selection))
            .setView(view)
            .setPositiveButton(R.string.cancel_btn) { _, _ -> listener?.onCancelConfirmed() }
            .create()
        settings.apply {

            if (isBackCameraInt ==  1) {
                view.findViewById<SettingsRadioButton>(R.id.front_camera_button)?.isChecked = true
                view.findViewById<SettingsRadioButton>(R.id.back_camera_button)?.isChecked = false
            } else {
                view.findViewById<SettingsRadioButton>(R.id.back_camera_button)?.isChecked = true
                view.findViewById<SettingsRadioButton>(R.id.front_camera_button)?.isChecked = false
            }
        }
        view.findViewById<SettingsRadioButton>(R.id.back_camera_button)?.setCheckedChangedListener {
            view.findViewById<SettingsRadioButton>(R.id.back_camera_button)?.isChecked = it
            view.findViewById<SettingsRadioButton>(R.id.front_camera_button)?.isChecked = false
            settings.apply { isBackCamera = requireActivity().getString(R.string.back_camera) }
            settings.apply { isBackCameraInt = 0 }
            listener?.OnCameraSelection()
            dialog.dismiss()
        }

        view.findViewById<SettingsRadioButton>(R.id.front_camera_button)
            ?.setCheckedChangedListener {
                view.findViewById<SettingsRadioButton>(R.id.front_camera_button)?.isChecked = it
                view.findViewById<SettingsRadioButton>(R.id.back_camera_button)?.isChecked = false
                settings.apply { isBackCamera = requireActivity().getString(R.string.front_camera) }
                settings.apply { isBackCameraInt = 1 }
                listener?.OnCameraSelection()
                dialog.dismiss()

            }

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        return dialog
    }
}