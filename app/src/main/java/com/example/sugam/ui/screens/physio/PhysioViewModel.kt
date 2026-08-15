package com.example.sugam.ui.screens.physio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugam.data.local.entity.PhysioEntity
import com.example.sugam.data.repository.PhysioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PhysioViewModel(
    private val physioRepository: PhysioRepository
) : ViewModel() {

    val physios: Flow<List<PhysioEntity>> =
        physioRepository.getAllPhysios()

    fun addPhysio(physio: PhysioEntity) {
        viewModelScope.launch {
            physioRepository.addPhysio(physio)
        }
    }

    fun updatePhysio(physio: PhysioEntity) {
        viewModelScope.launch {
            physioRepository.updatePhysio(physio)
        }
    }

    fun deletePhysio(physio: PhysioEntity) {
        viewModelScope.launch {
            physioRepository.deletePhysio(physio)
        }
    }

    fun setPhysioActive(physio: PhysioEntity, active: Boolean) {
        viewModelScope.launch {
            physioRepository.updatePhysio(
                physio.copy(active = active)
            )
        }
    }
}