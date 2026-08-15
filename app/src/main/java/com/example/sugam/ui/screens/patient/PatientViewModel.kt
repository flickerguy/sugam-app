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

class PatientViewModel(
    private val patientRepository: PatientRepository,
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    val patients: Flow<List<PatientEntity>> =
        patientRepository.getAllPatients()

    private val _patientAppointments =
        MutableStateFlow<List<PatientAppointmentHistory>>(emptyList())

    val patientAppointments: StateFlow<List<PatientAppointmentHistory>> =
        _patientAppointments

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

    fun loadPatientAppointments(patientId: Long) {
        viewModelScope.launch {
            appointmentRepository
                .getPatientAppointmentHistory(patientId)
                .collect { appointments ->
                    _patientAppointments.value = appointments
                }
        }
    }

}