package com.dt.auto.background.video.recorder.app_ui.activity.permission

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.permissionx.guolindev.PermissionX
import com.dt.auto.background.video.recorder.app_ui.activity.DashboardActivity
import com.dt.auto.background.video.recorder.databinding.ActivityPermissionBinding
import com.dt.auto.background.video.recorder.helpers.utils.*

class PermissionActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityPermissionBinding::inflate)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        handleClick()

    }

    private fun handleClick() {

        binding.btnContinue.setOnClickListenerCoolDown {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionX.init(this)
                    .permissions( Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_VIDEO)
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            startActivity(Intent(this@PermissionActivity, DashboardActivity::class.java))
                        }
                    }
            }else{
                PermissionX.init(this)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            startActivity(Intent(this@PermissionActivity, DashboardActivity::class.java))
                        }
                    }
            }
        }
    }
}