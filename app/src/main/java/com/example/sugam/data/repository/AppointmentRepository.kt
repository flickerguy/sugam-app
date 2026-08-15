package com.example.sugam.data.repository

import com.example.sugam.data.local.dao.AppointmentDao
import com.example.sugam.data.local.entity.AppointmentEntity
import com.example.sugam.data.local.model.PatientAppointmentHistory
import kotlinx.coroutines.flow.Flow

class AppointmentRepository(
    private val appointmentDao: AppointmentDao
) {

    fun getAllAppointments(): Flow<List<AppointmentEntity>> {
        return appointmentDao.getAllAppointments()
    }

    suspend fun addAppointment(appointment: AppointmentEntity) {
        appointmentDao.insertAppointment(appointment)
    }

    suspend fun deleteAppointment(appointmentId: Long) {
        appointmentDao.deleteAppointment(appointmentId)
    }

    fun getAppointmentsForDate(
        date: String
    ): Flow<List<AppointmentEntity>> =
        appointmentDao.getAppointmentsForDate(date)

    suspend fun countConflictingAppointments(
        physioId: Long,
        appointmentDate: String,
        startTime: String,
        endTime: String
    ): Int {
        return appointmentDao.countConflictingAppointments(
            physioId = physioId,
            appointmentDate = appointmentDate,
            startTime = startTime,
            endTime = endTime
        )
    }

    suspend fun cancelAppointment(
        appointmentId: Long
    ) {
        appointmentDao.cancelAppointment(appointmentId)
    }

    suspend fun updateAppointmentStatus(
        appointmentId: Long,
        status: String
    ) {
        appointmentDao.updateAppointmentStatus(
            appointmentId = appointmentId,
            status = status
        )
    }


    suspend fun updateAppointment(
        appointmentId: Long,
        patientId: Long,
        physioId: Long,
        appointmentDate: String,
        startTime: String,
        endTime: String,
        status: String,
        notes: String?
    ) {
        appointmentDao.updateAppointment(
            appointmentId = appointmentId,
            patientId = patientId,
            physioId = physioId,
            appointmentDate = appointmentDate,
            startTime = startTime,
            endTime = endTime,
            status = status,
            notes = notes
        )
    }

    suspend fun countConflictingAppointmentsForUpdate(
        appointmentId: Long,
        physioId: Long,
        appointmentDate: String,
        startTime: String,
        endTime: String
    ): Int {
        return appointmentDao.countConflictingAppointmentsForUpdate(
            appointmentId = appointmentId,
            physioId = physioId,
            appointmentDate = appointmentDate,
            startTime = startTime,
            endTime = endTime
        )
    }

    fun getAppointmentsForPatient(
        patientId: Long
    ): Flow<List<AppointmentEntity>> =
        appointmentDao.getAppointmentsForPatient(patientId)

    fun getPatientAppointmentHistory(
        patientId: Long
    ): Flow<List<PatientAppointmentHistory>> =
        appointmentDao.getPatientAppointmentHistory(patientId)
}