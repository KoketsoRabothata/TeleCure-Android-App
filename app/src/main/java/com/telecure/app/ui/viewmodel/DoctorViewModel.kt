package com.telecure.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telecure.app.data.model.Doctor
import com.telecure.app.data.repository.DoctorRepository
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {
    private val repo = DoctorRepository()
    private var allDoctors: List<Doctor> = emptyList()

    var doctors by mutableStateOf<List<Doctor>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set
    var selectedSpecialty by mutableStateOf("All"); private set
    var selectedLanguage by mutableStateOf("All"); private set
    var searchTerm by mutableStateOf(""); private set
    var selectedDoctor by mutableStateOf<Doctor?>(null); private set

    val specialties = listOf("All", "General Physician", "Cardiologist", "Dermatologist", "Pediatrician", "Psychiatrist", "Dentist")
    val languages: List<String> get() = listOf("All") + allDoctors.flatMap { it.languages }.distinct().sorted()

    fun load() {
        viewModelScope.launch {
            isLoading = true
            allDoctors = repo.getDoctors()
            applyFilters()
            isLoading = false
        }
    }

    fun filterBySpecialty(specialty: String) {
        selectedSpecialty = specialty
        applyFilters()
    }

    fun filterByLanguage(language: String) {
        selectedLanguage = language
        applyFilters()
    }

    fun search(term: String) {
        searchTerm = term
        applyFilters()
    }

    private fun applyFilters() {
        doctors = allDoctors.filter { doc ->
            (selectedSpecialty == "All" || doc.specialty == selectedSpecialty) &&
            (selectedLanguage == "All" || doc.languages.contains(selectedLanguage)) &&
            (searchTerm.isBlank() || doc.name.contains(searchTerm, true) || doc.specialty.contains(searchTerm, true))
        }
    }

    fun loadDoctor(uid: String) {
        viewModelScope.launch { selectedDoctor = repo.getDoctor(uid) }
    }
}
