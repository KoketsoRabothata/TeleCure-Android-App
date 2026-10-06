package com.telecure.app.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.MedicationReminder
import com.telecure.app.data.repository.ReminderRepository
import com.telecure.app.data.service.ReminderAlarmScheduler
import kotlinx.coroutines.launch

class RemindersViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ReminderRepository()

    var reminders by mutableStateOf<List<MedicationReminder>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            reminders = repo.getReminders(uid)
            isLoading = false
        }
    }

    fun addReminder(medicineName: String, dosage: String, times: List<String>) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (medicineName.isBlank() || times.isEmpty()) return
        viewModelScope.launch {
            val reminder = MedicationReminder(patientUid = uid, medicineName = medicineName, dosage = dosage, times = times)
            val result = repo.addReminder(reminder)
            result.onSuccess { saved -> ReminderAlarmScheduler.scheduleAll(getApplication(), saved) }
            load()
        }
    }

    fun toggleActive(reminder: MedicationReminder) {
        viewModelScope.launch {
            val newActive = !reminder.active
            repo.setActive(reminder.id, newActive)
            if (newActive) {
                ReminderAlarmScheduler.scheduleAll(getApplication(), reminder.copy(active = true))
            } else {
                ReminderAlarmScheduler.cancelAll(getApplication(), reminder)
            }
            load()
        }
    }

    fun deleteReminder(reminder: MedicationReminder) {
        viewModelScope.launch {
            ReminderAlarmScheduler.cancelAll(getApplication(), reminder)
            repo.deleteReminder(reminder.id)
            load()
        }
    }
}
