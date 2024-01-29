package com.dt.auto.background.video.recorder.helpers.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.dt.auto.background.video.recorder.service.NoScreenVideoRecorderService
import java.util.*


class VideoSchedulingAlarm : BroadcastReceiver() {
    private val REMINDER_BUNDLE = "MyReminderBundle"


    override fun onReceive(context: Context?, intent: Intent?) {
        // here you can get the extras you passed in when creating the alarm
        //intent.getBundleExtra(REMINDER_BUNDLE));

        Log.d("alaram","alarm manager fire")
        Toast.makeText(context, "Alarm went off", Toast.LENGTH_SHORT).show()

        sendCommandToService(context,Constants.ACTION_START_SERVICE)

        startTimer(context)
    }

    private fun startTimer(context: Context?) {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i("alaram", "Countdown seconds remaining: " + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                Log.i("alaram", "Timer finished")
                sendCommandToService(context,Constants.ACTION_STOP_SERVICE)
            }
        }.start()
    }

    private fun sendCommandToService(context: Context?, action: String) =
        Intent(
            context,
            NoScreenVideoRecorderService::class.java
        ).also {
            it.action = action
            context?.startService(it)
        }

}

