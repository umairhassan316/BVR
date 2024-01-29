package com.dt.auto.background.video.recorder.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dt.auto.background.video.recorder.R
import com.dt.auto.background.video.recorder.app_ui.activity.DashboardActivity
import com.dt.auto.background.video.recorder.helpers.utils.Constants.ACTION_MOVE_TO_RECORDINGS_LIST
import com.dt.auto.background.video.recorder.helpers.utils.Constants.NOTIFICATION_CHANNEL_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {


    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(
        @ApplicationContext app: Context
    ) = PendingIntent.getActivity(
        app,
        0,
        Intent(app, DashboardActivity::class.java).also {
            it.action = ACTION_MOVE_TO_RECORDINGS_LIST
        },
        flags
    )

    private val flags = when{
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            PendingIntent.FLAG_IMMUTABLE
        else -> {PendingIntent.FLAG_MUTABLE}
    }

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_stop)
        .setContentTitle("Background Video App")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)
}














