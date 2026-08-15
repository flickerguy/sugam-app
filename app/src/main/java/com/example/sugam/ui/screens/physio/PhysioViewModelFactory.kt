package com.example.sugam.ui.screens.physio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sugam.data.repository.PhysioRepository

class PhysioViewModelFactory(
    private val physioRepository: PhysioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhysioViewModel::class.java)) {
            return PhysioViewModel(physioRepository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}