package com.example.sugam.data.repository

import com.example.sugam.data.local.dao.ClinicSettingsDao
import com.example.sugam.data.local.entity.ClinicSettingsEntity
import kotlinx.coroutines.flow.Flow

class ClinicSettingsRepository(
    private val clinicSettingsDao: ClinicSettingsDao
) {

    fun observeSettings(): Flow<ClinicSettingsEntity?> =
        clinicSettingsDao.observeSettings()

    suspend fun updateSettings(
        settings: ClinicSettingsEntity
    ) {
        clinicSettingsDao.updateSettings(settings)
    }
}