package com.example.sugam.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.data.repository.PatientRepository
import com.example.sugam.data.repository.PhysioRepository

class HomeViewModelFactory(
    private val appointmentRepository: AppointmentRepository,
    private val patientRepository: PatientRepository,
    private val physioRepository: PhysioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                appointmentRepository,
                patientRepository,
                physioRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
