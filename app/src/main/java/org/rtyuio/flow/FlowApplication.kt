package org.rtyuio.flow

import android.app.Application
import android.content.Intent
import androidx.core.app.NotificationCompat
import org.rtyuio.flow.MainActivity
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val launchIntent = Intent(
            applicationContext,
            MainActivity::class.java
        )
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)

        applicationContext.startActivity(launchIntent)

        NotificationCompat.Builder(this, )

    }
}