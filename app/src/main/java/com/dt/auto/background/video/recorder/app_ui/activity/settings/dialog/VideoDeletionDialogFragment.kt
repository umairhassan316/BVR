package com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.dt.auto.background.video.recorder.R

class VideoDeletionDialogFragment : DialogFragment() {

    companion object {

        fun newInstance(): VideoDeletionDialogFragment {
            return VideoDeletionDialogFragment().apply {
                isCancelable = false
            }
        }
    }

    interface Listener {
        fun onDeletionConfirmed()
        fun onCancelConfirmed()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = requireActivity() as? Listener ?: parentFragment as? Listener
        val view = layoutInflater.inflate(R.layout.delete_video_recording_dialog, null, false)

        val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setTitle(getString(R.string.delete_title))
            .setView(view)
            .setPositiveButton(getString(R.string.deletion)) { _, _ -> listener?.onDeletionConfirmed() }
            .setNegativeButton(R.string.cancel_btn) { _, _ -> listener?.onCancelConfirmed() }
            .create()


        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }

        return dialog
    }
}