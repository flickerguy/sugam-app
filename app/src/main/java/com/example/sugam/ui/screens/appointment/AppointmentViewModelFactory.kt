package com.example.sugam.ui.screens.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.data.repository.ClinicSettingsRepository
import com.example.sugam.data.repository.PatientRepository
import com.example.sugam.data.repository.PhysioRepository

class AppointmentViewModelFactory(
    private val appointmentRepository: AppointmentRepository,
    private val patientRepository: PatientRepository,
    private val physioRepository: PhysioRepository,
    private val clinicSettingsRepository: ClinicSettingsRepository
) : ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        return AppointmentViewModel(
            appointmentRepository,
            patientRepository,
            physioRepository,
            clinicSettingsRepository
        ) as T
    }
}