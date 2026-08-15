package com.example.sugam.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sugam.data.local.entity.PatientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Query("SELECT * FROM patients ORDER BY name")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Insert
    suspend fun insertPatient(patient: PatientEntity)

    @Query("DELETE FROM patients WHERE id = :patientId")
    suspend fun deletePatient(patientId: Long)
}