package com.example.sugam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clinic_settings")
data class ClinicSettingsEntity(
    @PrimaryKey
    val id: Int = 1,

    val clinicName: String = "Sugam Physiotherapy Clinic",

    val workingStartTime: String = "09:00",

    val workingEndTime: String = "19:00",

    val appointmentDurationMinutes: Int = 30
)