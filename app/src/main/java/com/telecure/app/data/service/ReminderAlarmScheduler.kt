package com.telecure.app.data.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.telecure.app.data.model.MedicationReminder
import java.util.Calendar

/**
 * Schedules/cancels the daily exact alarms behind medication reminders.
 * One alarm is scheduled per time-of-day in the reminder (e.g. 08:00 and 20:00 -> two alarms).
 * On Android 12+, the user must grant the "Alarms & reminders" special permission for exact
 * alarms to fire on time — if not granted, reminders will still show but may be delayed by the
 * system's battery optimizations.
 */
object ReminderAlarmScheduler {

    private fun requestCode(reminderId: String, time: String): Int = (reminderId + time).hashCode()

    private fun buildIntent(context: Context, reminder: MedicationReminder, time: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_SHOW_REMINDER
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(ReminderReceiver.EXTRA_MEDICINE_NAME, reminder.medicineName)
            putExtra(ReminderReceiver.EXTRA_DOSAGE, reminder.dosage)
            putExtra(ReminderReceiver.EXTRA_TIME, time)
        }
        return PendingIntent.getBroadcast(
            context, requestCode(reminder.id, time), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextTriggerMillis(time: String): Long {
        val parts = time.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cal.timeInMillis <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    fun scheduleAll(context: Context, reminder: MedicationReminder) {
        if (!reminder.active) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        reminder.times.forEach { time ->
            val pendingIntent = buildIntent(context, reminder, time)
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerMillis(time), pendingIntent)
            } catch (e: SecurityException) {
                // Exact-alarm permission not granted — the reminder simply won't fire until the
                // person grants "Alarms & reminders" for this app in system settings.
            }
        }
    }

    /** Called by ReminderReceiver right after showing today's notification, to queue tomorrow's. */
    fun rescheduleNextDay(context: Context, reminderId: String, medicineName: String, dosage: String, time: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_SHOW_REMINDER
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminderId)
            putExtra(ReminderReceiver.EXTRA_MEDICINE_NAME, medicineName)
            putExtra(ReminderReceiver.EXTRA_DOSAGE, dosage)
            putExtra(ReminderReceiver.EXTRA_TIME, time)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode(reminderId, time), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
        } catch (e: SecurityException) { /* see note above */ }
    }

    fun cancelAll(context: Context, reminder: MedicationReminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        reminder.times.forEach { time ->
            alarmManager.cancel(buildIntent(context, reminder, time))
        }
    }
}
