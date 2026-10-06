package com.telecure.app.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.telecure.app.MainActivity

/**
 * Fires when a scheduled medication time is reached. Shows a notification with a
 * "Mark as Taken" action, then immediately reschedules the same time slot for tomorrow.
 */
class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SHOW_REMINDER = "com.telecure.app.ACTION_SHOW_REMINDER"
        const val ACTION_MARK_TAKEN = "com.telecure.app.ACTION_MARK_TAKEN"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_MEDICINE_NAME = "extra_medicine_name"
        const val EXTRA_DOSAGE = "extra_dosage"
        const val EXTRA_TIME = "extra_time"
        const val CHANNEL_ID = "medication_reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(EXTRA_REMINDER_ID) ?: return
        val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME).orEmpty()
        val dosage = intent.getStringExtra(EXTRA_DOSAGE).orEmpty()
        val time = intent.getStringExtra(EXTRA_TIME).orEmpty()

        when (intent.action) {
            ACTION_MARK_TAKEN -> {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(reminderId.hashCode())
                // The in-app "Reminders" list also lets the person view/manage taken history;
                // this action just dismisses today's notification.
            }
            else -> {
                showNotification(context, reminderId, medicineName, dosage)
                ReminderAlarmScheduler.rescheduleNextDay(context, reminderId, medicineName, dosage, time)
            }
        }
    }

    private fun showNotification(context: Context, reminderId: String, medicineName: String, dosage: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Medication Reminders", NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context, reminderId.hashCode(), openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val markTakenIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_MARK_TAKEN
            putExtra(EXTRA_REMINDER_ID, reminderId)
        }
        val markTakenPendingIntent = PendingIntent.getBroadcast(
            context, reminderId.hashCode() + 1, markTakenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time to take $medicineName")
            .setContentText(dosage.ifBlank { "Tap Mark as Taken once you've taken it." })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, "Mark as Taken", markTakenPendingIntent)
            .build()

        nm.notify(reminderId.hashCode(), notification)
    }
}
