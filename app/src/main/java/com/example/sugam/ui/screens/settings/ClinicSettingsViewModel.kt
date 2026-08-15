package com.example.sugam.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugam.data.local.entity.ClinicSettingsEntity
import com.example.sugam.data.repository.ClinicSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClinicSettingsViewModel(
    private val repository: ClinicSettingsRepository
) : ViewModel() {

    val settings: StateFlow<ClinicSettingsEntity?> =
        repository.observeSettings()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

    fun updateSettings(
        clinicName: String,
        workingStartTime: String,
        workingEndTime: String,
        appointmentDurationMinutes: Int
    ) {
        viewModelScope.launch {
            val current = settings.value ?: return@launch

            repository.updateSettings(
                current.copy(
                    clinicName = clinicName,
                    workingStartTime = workingStartTime,
                    workingEndTime = workingEndTime,
                    appointmentDurationMinutes = appointmentDurationMinutes
                )
            )
        }
    }
}