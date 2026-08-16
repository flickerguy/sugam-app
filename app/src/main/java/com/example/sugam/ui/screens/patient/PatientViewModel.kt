package com.example.sugam.ui.screens.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugam.data.local.entity.AppointmentEntity
import com.example.sugam.data.local.entity.PatientEntity
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.data.repository.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.sugam.data.local.model.PatientAppointmentHistory

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.flow.combine

@OptIn(ExperimentalCoroutinesApi::class)
class PatientViewModel(
    private val patientRepository: PatientRepository,
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val patients: StateFlow<List<PatientEntity>> =
        combine(
            patientRepository.getAllPatients(),
            _searchQuery
        ) { patients, query ->
            if (query.isBlank()) {
                patients
            } else {
                patients.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.phone.contains(query)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _selectedPatientId = MutableStateFlow<Long?>(null)

    val selectedPatient: StateFlow<PatientEntity?> =
        _selectedPatientId
            .flatMapLatest { id ->
                if (id == null) kotlinx.coroutines.flow.flowOf(null)
                else patientRepository.getPatientById(id)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    val patientAppointments: StateFlow<List<PatientAppointmentHistory>> =
        _selectedPatientId
            .flatMapLatest { id ->
                if (id == null) kotlinx.coroutines.flow.flowOf(emptyList())
                else appointmentRepository.getPatientAppointmentHistory(id)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun addPatient(patient: PatientEntity) {
        viewModelScope.launch {
            patientRepository.addPatient(patient)
        }
    }

    fun deletePatient(patientId: Long) {
        viewModelScope.launch {
            patientRepository.deletePatient(patientId)
        }
    }

    fun setPatientId(patientId: Long?) {
        _selectedPatientId.value = patientId
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

}
