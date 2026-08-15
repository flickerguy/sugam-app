package com.example.sugam.data.local.model

data class PatientAppointmentHistory(
    val id: Long,
    val appointmentDate: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val notes: String?,
    val physioName: String
)