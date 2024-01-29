package com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.di.settings

class ScheduleDurationDialog : DialogFragment() {

    companion object {

        fun newInstance(): ScheduleDurationDialog {
            return ScheduleDurationDialog().apply {
                isCancelable = false
            }
        }
    }

    interface Listener {
        fun onDoneDuration()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = requireActivity() as? Listener ?: parentFragment as? Listener
        val view = layoutInflater.inflate(R.layout.schedule_duration_dialog, null, false)

        val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setView(view)
            .setPositiveButton(R.string.ok_btn) { _, _ ->
                settings.apply { scheduleDuration =
                    view.findViewById<SeekBar>(R.id.sb_duration).progress.toString()
                }
                view.findViewById<TextView>(R.id.seekbar_time).text =
                    view.findViewById<SeekBar>(R.id.sb_duration).progress.toString()+" Min"
                listener?.onDoneDuration()
            }
            .setNegativeButton(R.string.cancel_btn) { _, _ -> dialog?.dismiss() }
            .create()


        view.findViewById<SeekBar>(R.id.sb_duration).
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                view.findViewById<TextView>(R.id.seekbar_time).text = "$progress Min"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
        return dialog
    }


}