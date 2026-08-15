package com.example.sugam.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sugam.data.local.entity.AppointmentEntity
import com.example.sugam.data.local.model.PatientAppointmentHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointments ORDER BY appointmentDate, startTime")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Insert
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments WHERE id = :appointmentId")
    suspend fun deleteAppointment(appointmentId: Long)

    @Query(
        """
    SELECT * FROM appointments
    WHERE appointmentDate = :date
    ORDER BY startTime
    """
    )
    fun getAppointmentsForDate(
        date: String
    ): Flow<List<AppointmentEntity>>

    @Query(
        """
    SELECT COUNT(*) FROM appointments
    WHERE physioId = :physioId
      AND appointmentDate = :appointmentDate
      AND status != 'CANCELLED'
      AND startTime < :endTime
      AND endTime > :startTime
    """
    )
    suspend fun countConflictingAppointments(
        physioId: Long,
        appointmentDate: String,
        startTime: String,
        endTime: String
    ): Int


    @Query(
        """
    UPDATE appointments
    SET status = 'CANCELLED'
    WHERE id = :appointmentId
    """
    )
    suspend fun cancelAppointment(
        appointmentId: Long
    )

    @Query(
        """
    UPDATE appointments
    SET status = :status
    WHERE id = :appointmentId
    """
    )
    suspend fun updateAppointmentStatus(
        appointmentId: Long,
        status: String
    )

    @Query(
        """
    UPDATE appointments
    SET patientId = :patientId,
        physioId = :physioId,
        appointmentDate = :appointmentDate,
        startTime = :startTime,
        endTime = :endTime,
        status = :status,
        notes = :notes
    WHERE id = :appointmentId
    """
    )
    suspend fun updateAppointment(
        appointmentId: Long,
        patientId: Long,
        physioId: Long,
        appointmentDate: String,
        startTime: String,
        endTime: String,
        status: String,
        notes: String?
    )

    @Query(
        """
    SELECT COUNT(*) FROM appointments
    WHERE id != :appointmentId
      AND physioId = :physioId
      AND appointmentDate = :appointmentDate
      AND status != 'CANCELLED'
      AND startTime < :endTime
      AND endTime > :startTime
    """
    )
    suspend fun countConflictingAppointmentsForUpdate(
        appointmentId: Long,
        physioId: Long,
        appointmentDate: String,
        startTime: String,
        endTime: String
    ): Int

    @Query(
        """
    SELECT * FROM appointments
    WHERE patientId = :patientId
    ORDER BY appointmentDate DESC, startTime DESC
    """
    )
    fun getAppointmentsForPatient(
        patientId: Long
    ): Flow<List<AppointmentEntity>>

    @Query(
        """
    SELECT
        a.id,
        a.appointmentDate,
        a.startTime,
        a.endTime,
        a.status,
        a.notes,
        p.name AS physioName
    FROM appointments a
    INNER JOIN physios p
        ON a.physioId = p.id
    WHERE a.patientId = :patientId
    ORDER BY a.appointmentDate DESC, a.startTime DESC
    """
    )
    fun getPatientAppointmentHistory(
        patientId: Long
    ): Flow<List<PatientAppointmentHistory>>


}