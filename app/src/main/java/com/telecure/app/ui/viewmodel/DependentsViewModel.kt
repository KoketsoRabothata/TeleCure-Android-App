package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.telecure.app.data.model.Dependent
import com.telecure.app.data.repository.DependentRepository
import kotlinx.coroutines.launch
import java.util.UUID

class DependentsViewModel : ViewModel() {
    private val repo = DependentRepository()

    var dependents by mutableStateOf<List<Dependent>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set

    fun load() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            dependents = repo.getDependents(uid)
            isLoading = false
        }
    }

    fun addDependent(name: String, relationship: String, dateOfBirth: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (name.isBlank()) return
        viewModelScope.launch {
            val dependent = Dependent(id = UUID.randomUUID().toString(), name = name, relationship = relationship, dateOfBirth = dateOfBirth)
            repo.addDependent(uid, dependent)
            load()
        }
    }

    fun deleteDependent(id: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            repo.deleteDependent(uid, id)
            load()
        }
    }
}
