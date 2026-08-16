package com.example.sugam.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.data.repository.PatientRepository
import com.example.sugam.data.repository.PhysioRepository
import com.example.sugam.domain.model.Appointment
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val appointmentRepository: AppointmentRepository,
    patientRepository: PatientRepository,
    physioRepository: PhysioRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filterDate = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
    val filterDate: StateFlow<String> = _filterDate

    val appointments: StateFlow<List<Appointment>> = combine(
        _filterDate.flatMapLatest { appointmentRepository.getAppointmentsForDate(it) },
        patientRepository.getAllPatients(),
        physioRepository.getAllPhysios(),
        _searchQuery
    ) { entities, patients, physios, query ->
        val patientMap = patients.associateBy { it.id }
        val physioMap = physios.associateBy { it.id }

        entities.map { entity ->
            Appointment(
                id = entity.id,
                patientId = entity.patientId,
                physioId = entity.physioId,
                appointmentDate = entity.appointmentDate,
                startTime = entity.startTime,
                endTime = entity.endTime,
                status = entity.status,
                notes = entity.notes,
                patientName = patientMap[entity.patientId]?.name ?: "Unknown",
                physioName = physioMap[entity.physioId]?.name ?: "Unknown"
            )
        }.filter {
            it.patientName.contains(query, ignoreCase = true) ||
            it.physioName.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCount: StateFlow<Int> = appointments.map { list ->
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        list.count { it.appointmentDate == todayStr && it.status != "CANCELLED" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val upcomingCount: StateFlow<Int> = appointments.map { list ->
        val now = LocalTime.now()
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        list.count { 
            it.appointmentDate == todayStr && 
            it.status != "CANCELLED" &&
            LocalTime.parse(it.startTime, DateTimeFormatter.ofPattern("HH:mm")).isAfter(now)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilterDate(date: String) {
        _filterDate.value = date
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            appointmentRepository.cancelAppointment(appointmentId)
        }
    }

    fun updateStatus(appointmentId: Long, status: String) {
        viewModelScope.launch {
            appointmentRepository.updateAppointmentStatus(appointmentId, status)
        }
    }
}
