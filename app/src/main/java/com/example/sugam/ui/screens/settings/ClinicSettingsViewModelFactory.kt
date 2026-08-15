package com.example.sugam.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sugam.data.repository.ClinicSettingsRepository

class ClinicSettingsViewModelFactory(
    private val repository: ClinicSettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ClinicSettingsViewModel::class.java)) {
            return ClinicSettingsViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}