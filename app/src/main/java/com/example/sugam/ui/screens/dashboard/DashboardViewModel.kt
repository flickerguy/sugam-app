package com.example.sugam.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.domain.model.Appointment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.lifecycle.viewModelScope
import com.example.sugam.data.local.entity.AppointmentEntity
import kotlinx.coroutines.launch
import com.example.sugam.data.repository.PatientRepository
import com.example.sugam.data.repository.PhysioRepository
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime

class DashboardViewModel(
    private val appointmentRepository: AppointmentRepository,
    private val patientRepository: PatientRepository,
    private val physioRepository: PhysioRepository
) : ViewModel() {

    val today = LocalDate.now()
        .format(DateTimeFormatter.ISO_LOCAL_DATE)
    val appointments: Flow<List<Appointment>> =
        combine(
            appointmentRepository.getAppointmentsForDate(today),
            patientRepository.getAllPatients(),
            physioRepository.getAllPhysios()
        ) { appointmentEntities, patients, physios ->

            val patientNames = patients.associateBy { it.id }
            val physioNames = physios.associateBy { it.id }

            appointmentEntities.map { entity ->
                Appointment(
                    id = entity.id,
                    patientId = entity.patientId,
                    physioId = entity.physioId,
                    appointmentDate = entity.appointmentDate,
                    startTime = entity.startTime,
                    endTime = entity.endTime,
                    status = entity.status,
                    notes = entity.notes,
                    patientName = patientNames[entity.patientId]?.name
                        ?: "Unknown Patient",
                    physioName = physioNames[entity.physioId]?.name
                        ?: "Unknown Physio"
                )
            }
        }

    fun addAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            appointmentRepository.addAppointment(appointment)
        }
    }

    val scheduledCount: StateFlow<Int> =
        appointments
            .map { list ->
                list.count { it.status == "SCHEDULED" }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                0
            )

    val confirmedCount: StateFlow<Int> =
        appointments
            .map { list ->
                list.count { it.status == "CONFIRMED" }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                0
            )

    val completedCount: StateFlow<Int> =
        appointments
            .map { list ->
                list.count { it.status == "COMPLETED" }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                0
            )

    val cancelledCount: StateFlow<Int> =
        appointments
            .map { list ->
                list.count { it.status == "CANCELLED" }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                0
            )

    val noShowCount: StateFlow<Int> =
        appointments
            .map { list ->
                list.count { it.status == "NO_SHOW" }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                0
            )


    val upcomingAppointments: Flow<List<Appointment>> =
        appointments.map { appointmentList ->
            val now = LocalTime.now()

            appointmentList
                .filter { appointment ->
                    val appointmentTime = LocalTime.parse(
                        appointment.startTime,
                        DateTimeFormatter.ofPattern("HH:mm")
                    )

                    appointmentTime.isAfter(now)
                }
                .sortedBy { appointment ->
                    appointment.startTime
                }
        }
}