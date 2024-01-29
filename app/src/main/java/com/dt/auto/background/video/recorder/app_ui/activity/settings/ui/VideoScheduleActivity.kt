package com.dt.auto.background.video.recorder.app_ui.activity.settings.ui


import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog.ScheduleDurationDialog
import com.dt.auto.background.video.recorder.base.BaseActivity
import com.dt.auto.background.video.recorder.databinding.ActivityVideoScheduleBinding
import com.dt.auto.background.video.recorder.di.settings
import com.dt.auto.background.video.recorder.helpers.utils.VideoSchedulingAlarm
import com.dt.auto.background.video.recorder.helpers.utils.viewBinding
import java.text.DateFormat
import java.util.*


class VideoScheduleActivity : BaseActivity(), DatePickerDialog.OnDateSetListener,ScheduleDurationDialog.Listener{

    var pendingIntent: PendingIntent? = null
    var alarmManager: AlarmManager? = null
    var scheduleDurationDialog: ScheduleDurationDialog? = null


    private val binding by viewBinding(ActivityVideoScheduleBinding::inflate)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        logEvent("VideoSchedule_activity")

        initUI()
        handleClicks()
        showBanner()

    }

    private fun initUI() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager?

        changeCamera()
        setDateTimeFromPref()

    }

    private fun setDateTimeFromPref() {

        binding.tvTime.text = settings.scheduletimeHr.toString()+":"+settings.scheduletimeMin
        binding.tvDate.text = settings.scheduleDate
        binding.tvDuration.text = settings.scheduleDuration +" min"

    }


    private fun handleClicks() {

        binding.btnTime.setOnClickListener {
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub
            val mcurrentTime: Calendar = Calendar.getInstance()
            val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute: Int = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog = TimePickerDialog(this@VideoScheduleActivity,
                { timePicker, selectedHour, selectedMinute ->
                    binding.tvTime.text = "$selectedHour:$selectedMinute"
                    settings.apply {
                        scheduletimeHr = selectedHour
                    }
                    settings.apply {
                        scheduletimeMin = selectedMinute
                    }
                },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }
        binding.btnDate.setOnClickListener {

            // Please note that use your package name here
            // Please note that use your package name here
            val mDatePickerDialogFragment: com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog.DatePicker =
                com.dt.auto.background.video.recorder.app_ui.activity.settings.dialog.DatePicker()
            mDatePickerDialogFragment.show(supportFragmentManager, "DATE PICK")

        }
        binding.btnDuration.setOnClickListener {
            scheduleDurationDialog = ScheduleDurationDialog.newInstance()
            scheduleDurationDialog?.show(supportFragmentManager, "")
        }
        binding.btnCamera.setOnClickListener {
            changeCamera()
        }


        binding.btnSave.setOnClickListener {
            saveAlarm()
        }

    }

    private fun saveAlarm() {

        if(settings.scheduletimeHr!=0) {
            val calendar: Calendar = Calendar.getInstance()
            if (Build.VERSION.SDK_INT >= 23) {
               calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),  settings.scheduletimeHr,
                   settings.scheduletimeMin, 0)
            }
            else {
                calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    settings.scheduletimeHr,
                    settings.scheduletimeMin, 0)
            }
            setAlarm(calendar.timeInMillis)


        }else{
            Toast.makeText(this, "Select Date & Time", Toast.LENGTH_SHORT).show()

        }

    }

    private fun setAlarm(timeInMillis: Long) {
        Log.d("alaram", "Alarm just set "+timeInMillis)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, VideoSchedulingAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flags)
        alarmManager.setRepeating(AlarmManager.RTC, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show()

        finish()
    }



    private val flags = when{
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            PendingIntent.FLAG_IMMUTABLE
        else -> {PendingIntent.FLAG_MUTABLE}
    }
    private fun changeCamera() {
        if(settings.isBackCameraInt==0) {
            settings.apply {
                isBackCamera = this@VideoScheduleActivity.getString(R.string.front_camera)
                binding.tvCamera.text = isBackCamera
            }
            settings.apply { isBackCameraInt = 1 }

        }else{
            settings.apply {
                isBackCamera = this@VideoScheduleActivity.getString(R.string.back_camera)
                binding.tvCamera.text = isBackCamera
            }
            settings.apply { isBackCameraInt = 0 }

        }
    }


    override fun onResume() {
        super.onResume()
        try {
            showNativeAds()
        } catch (e: Exception) { }

    }

    override fun onDateSet(
        view: android.widget.DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) {
        val mCalendar = Calendar.getInstance()
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = month
        mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        val selectedDate: String =
            DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.time)
        binding.tvDate.text = selectedDate
        settings.apply {
            scheduleDate = selectedDate
        }
    }

    override fun onDoneDuration() {
        scheduleDurationDialog?.dismiss()
        binding.tvDuration.text = settings.scheduleDuration+" min"
    }


}