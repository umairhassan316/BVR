package com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.dt.auto.background.video.recorder.R

class OverlayDialogFragment : DialogFragment() {

    companion object {

        fun newInstance(): OverlayDialogFragment {
            return OverlayDialogFragment().apply {
                isCancelable = true
            }
        }
    }

    interface Listener {
        fun onPermissionCheck()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = requireActivity() as? Listener ?: parentFragment as? Listener
        val view = layoutInflater.inflate(R.layout.overlay_new_dialog, null, false)

        val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setView(view)
            .create()

        val switch = view.findViewById<SwitchCompat>(R.id.switch_button)
        val btnAllow = view.findViewById<Button>(R.id.btnAllow)

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            listener?.onPermissionCheck()
            dialog.dismiss()
        }

        btnAllow.setOnClickListener {
            listener?.onPermissionCheck()
            dialog.dismiss()
        }


        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
        return dialog
    }


}