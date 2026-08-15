package com.example.sugam.data.repository

import com.example.sugam.data.local.dao.PatientDao
import com.example.sugam.data.local.entity.PatientEntity
import kotlinx.coroutines.flow.Flow

class PatientRepository(
    private val patientDao: PatientDao
) {

    fun getAllPatients(): Flow<List<PatientEntity>> {
        return patientDao.getAllPatients()
    }

    suspend fun addPatient(patient: PatientEntity) {
        patientDao.insertPatient(patient)
    }

    suspend fun deletePatient(patientId: Long) {
        patientDao.deletePatient(patientId)
    }
}