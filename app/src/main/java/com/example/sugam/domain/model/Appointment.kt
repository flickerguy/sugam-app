package com.example.sugam.domain.model

data class Appointment(
    val id: Long,
    val patientId: Long,
    val physioId: Long,
    val appointmentDate: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val notes: String?,
    val patientName: String,
    val physioName: String
)