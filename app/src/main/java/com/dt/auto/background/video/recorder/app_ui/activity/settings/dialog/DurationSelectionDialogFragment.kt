package com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.helpers.VersionCheckHelper
import com.dt.auto.background.video.recorder.app_ui.in_apps.BillingPurchases.isPurchase
import com.dt.auto.background.video.recorder.app_ui.in_apps.InAppScreen
import com.dt.auto.background.video.recorder.helpers.utils.invisible
import com.dt.auto.background.video.recorder.helpers.utils.log
import com.dt.auto.background.video.recorder.helpers.utils.setOnClickListenerCoolDown

class DurationSelectionDialogFragment : DialogFragment() {

    companion object {

        fun newInstance(): DurationSelectionDialogFragment {
            return DurationSelectionDialogFragment().apply {
                isCancelable = false
            }
        }
    }

    interface Listener {
        fun onDoneDuration()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = requireActivity() as? Listener ?: parentFragment as? Listener
        val view = layoutInflater.inflate(R.layout.recording_duration_new_dialog, null, false)

        val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setView(view)
            .setPositiveButton(R.string.ok_btn) { _, _ -> listener?.onDoneDuration() }
            .setNegativeButton(R.string.cancel_btn) { _, _ -> dialog?.dismiss() }
            .create()

        val rGroup = view.findViewById<RadioGroup>(R.id.radio_group)

        settings.apply {
            when (recordingDuration) {
                "120" -> {
                    rGroup.findViewById<RadioButton>(R.id.onetwenty_minutes_rb).isChecked = true
                }
                "60" -> {
                    rGroup.findViewById<RadioButton>(R.id.sixty_minutes_rb).isChecked = true
                }
                "45" -> {
                    rGroup.findViewById<RadioButton>(R.id.fourty_five_minutes_rb).isChecked = true
                }
                "30" -> {
                    rGroup.findViewById<RadioButton>(R.id.thirty_minutes_rb).isChecked = true
                }
                "15" -> {
                    rGroup.findViewById<RadioButton>(R.id.fifteen_minutes_rb).isChecked = true
                }
                "10" -> {
                    rGroup.findViewById<RadioButton>(R.id.ten_minutes_rb).isChecked = true
                }
                "5" -> {
                    rGroup.findViewById<RadioButton>(R.id.five_minutes_rb).isChecked = true
                }
                else -> {
                    rGroup.findViewById<RadioButton>(R.id.sixty_minutes_rb).isChecked = true
                    recordingDuration = "60"
                }
            }
        }


        rGroup.setOnCheckedChangeListener { group, checkedId ->
            // This will get the radiobutton that has changed in its check state
            val checkedRadioButton = group.findViewById<RadioButton>(checkedId)
            // This puts the value (true/false) into the variable
            val isChecked = checkedRadioButton.isChecked
            when (checkedRadioButton) {
                view.findViewById<RadioButton>(R.id.onetwenty_minutes_rb) -> {
                    if (isChecked) {
                        log("""Checked: ${checkedRadioButton.text}""")
                        settings.apply { recordingDuration = "120" }

                    }

                }
                view.findViewById<RadioButton>(R.id.sixty_minutes_rb) -> {
                    if (isChecked) {
                        log("""Checked: ${checkedRadioButton.text}""")
                        settings.apply { recordingDuration = "60" }

                    }

                }
                view.findViewById<RadioButton>(R.id.fourty_five_minutes_rb) -> {
                    if (isChecked) {
                        log("""Checked: ${checkedRadioButton.text}""")
                        settings.apply { recordingDuration = "45" }

                    }

                }
                view.findViewById<RadioButton>(R.id.thirty_minutes_rb) -> {
                    if (isChecked) {
                        log("""Checked: ${checkedRadioButton.text}""")
                        settings.apply { recordingDuration = "30" }

                    }

                }
                view.findViewById<RadioButton>(R.id.fifteen_minutes_rb) -> {
                    if (isChecked) {
                        log("""Checked: ${checkedRadioButton.text}""")
                        settings.apply { recordingDuration = "15" }

                    }

                }
                view.findViewById<RadioButton>(R.id.ten_minutes_rb) -> {
                    if (isChecked) {
                        log("""Checked: ${checkedRadioButton.text}""")
                        settings.apply { recordingDuration = "10" }

                    }

                }
                view.findViewById<RadioButton>(R.id.five_minutes_rb) -> {
                    if (isChecked) {
                        log("""Checked: ${checkedRadioButton.text}""")
                        settings.apply { recordingDuration = "5" }

                    }

                }
                view.findViewById<ImageView>(R.id.pro_iv) -> {
                    startActivity(Intent(requireActivity(), InAppScreen::class.java))
                    dialog.dismiss()
                }
            }

        }


        if (isPurchase) {
//            settings.apply { recordingDuration = "120" }
            view.findViewById<ImageView>(R.id.pro_iv).invisible()
            view.findViewById<RadioButton>(R.id.onetwenty_minutes_rb).visibility= View.VISIBLE
        }

        view.findViewById<TextView>(R.id.tv_unlimitedtime).setOnClickListener {
            settings.apply { recordingDuration = "120" }
        }

        view.findViewById<ImageView>(R.id.pro_iv).setOnClickListener {
            if (!isPurchase) {
                startActivity(Intent(requireActivity(), InAppScreen::class.java))
                dialog.dismiss()
            }
        }

        view.findViewById<TextView>(R.id.unlimited_time).setOnClickListenerCoolDown {
            if (!isPurchase) {
                startActivity(Intent(requireActivity(), InAppScreen::class.java))
                dialog.dismiss()
            } else {
                view.findViewById<TextView>(R.id.tv_unlimitedtime).visibility= View.VISIBLE
                view.findViewById<ImageView>(R.id.pro_iv).visibility= View.INVISIBLE
                settings.apply { recordingDuration = "120" }
                view.findViewById<ImageView>(R.id.pro_iv).invisible()
                if (VersionCheckHelper.isMarshmallowPlus()){
                    view.findViewById<TextView>(R.id.unlimited_time).setBackgroundColor(requireActivity().resources.getColor(R.color.light_blue_100, null))
                    view.findViewById<TextView>(R.id.unlimited_time).setTextColor(requireActivity().resources.getColor(R.color.white, null))

                }else{
                    view.findViewById<TextView>(R.id.unlimited_time).setBackgroundColor(requireActivity().resources.getColor(R.color.light_blue_100))
                    view.findViewById<TextView>(R.id.unlimited_time).setTextColor(requireActivity().resources.getColor(R.color.white))

                }
            }
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