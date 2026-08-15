package com.example.sugam.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.sugam.data.local.entity.ClinicSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicSettingsDao {

@Query("SELECT * FROM clinic_settings WHERE id = 1")
fun observeSettings(): Flow<ClinicSettingsEntity?>

@Update
suspend fun updateSettings(settings: ClinicSettingsEntity)
}