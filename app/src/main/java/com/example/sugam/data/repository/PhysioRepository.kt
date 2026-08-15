package com.example.sugam.data.repository

import com.example.sugam.data.local.dao.PhysioDao
import com.example.sugam.data.local.entity.PhysioEntity
import kotlinx.coroutines.flow.Flow

class PhysioRepository(
    private val physioDao: PhysioDao
) {

    fun getAllPhysios(): Flow<List<PhysioEntity>> {
        return physioDao.getAllPhysios()
    }

    suspend fun addPhysio(physio: PhysioEntity) {
        physioDao.insertPhysio(physio)
    }

    suspend fun updatePhysio(physio: PhysioEntity) {
        physioDao.updatePhysio(physio)
    }

    suspend fun deletePhysio(physio: PhysioEntity) {
        physioDao.deletePhysio(physio)
    }
}