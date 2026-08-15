package com.example.sugam.ui.screens.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.data.repository.PatientRepository

class PatientViewModelFactory(
    private val patientRepository: PatientRepository,
    private val appointmentRepository: AppointmentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
            return PatientViewModel(
                patientRepository = patientRepository,
                appointmentRepository = appointmentRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}