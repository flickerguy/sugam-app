package com.example.sugam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val patientId: Long,
    val physioId: Long,

    val appointmentDate: String,
    val startTime: String,
    val endTime: String,

    val status: String,
    val notes: String? = null
)