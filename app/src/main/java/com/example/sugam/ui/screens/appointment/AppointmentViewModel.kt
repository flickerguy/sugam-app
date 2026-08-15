package com.example.sugam.ui.screens.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugam.data.local.entity.PatientEntity
import com.example.sugam.data.local.entity.PhysioEntity
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.data.repository.PatientRepository
import com.example.sugam.data.repository.PhysioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.sugam.data.local.entity.AppointmentEntity
import com.example.sugam.data.repository.ClinicSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class AppointmentViewModel(
    private val appointmentRepository: AppointmentRepository,
    patientRepository: PatientRepository,
    physioRepository: PhysioRepository,
    clinicSettingsRepository: ClinicSettingsRepository
): ViewModel() {

    val patients: Flow<List<PatientEntity>> =
        patientRepository.getAllPatients()

    val clinicSettings =
        clinicSettingsRepository.observeSettings()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

    private val _selectedPatientId = MutableStateFlow<Long?>(null)

    val selectedPatientId: StateFlow<Long?> = _selectedPatientId

    fun selectPatient(patientId: Long) {
        _selectedPatientId.value = patientId
    }

    val physios: Flow<List<PhysioEntity>> =
        physioRepository.getAllPhysios()

    private val _selectedPhysioId = MutableStateFlow<Long?>(null)

    val selectedPhysioId: StateFlow<Long?> = _selectedPhysioId

    private var editingAppointmentStatus: String = "SCHEDULED"

    fun selectPhysio(physioId: Long) {
        _selectedPhysioId.value = physioId
    }

    private val _selectedAppointmentDate = MutableStateFlow<String?>(null)

    val selectedAppointmentDate: StateFlow<String?> =
        _selectedAppointmentDate

    fun selectAppointmentDate(date: String) {
        _selectedAppointmentDate.value = date
    }

    private val _selectedStartTime = MutableStateFlow<String?>(null)

    val selectedStartTime: StateFlow<String?> =
        _selectedStartTime


    private val _saveMessage = MutableStateFlow<String?>(null)
    val saveMessage: StateFlow<String?> = _saveMessage

    private val _viewDate = MutableStateFlow(
        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    )

    val viewDate: StateFlow<String> = _viewDate

    private val _editingAppointmentId =
        MutableStateFlow<Long?>(null)

    val editingAppointmentId: StateFlow<Long?> =
        _editingAppointmentId

    fun selectViewDate(date: String) {
        _viewDate.value = date
    }

    fun selectStartTime(time: String) {
        _selectedStartTime.value = time
    }

    fun calculateEndTime(
        startTime: String,
        durationMinutes: Int
    ): String? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val start = LocalTime.parse(startTime, formatter)

            val end = start.plusMinutes(durationMinutes.toLong())

            end.format(formatter)
        } catch (e: Exception) {
            null
        }
    }

    fun getPatientName(patientId: Long?): String {
        if (patientId == null) return ""

        // This will be replaced by a reactive lookup later.
        return ""
    }

    val appointments: StateFlow<List<AppointmentEntity>> =
        viewDate
            .flatMapLatest { date ->
                appointmentRepository.getAppointmentsForDate(date)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun clearSaveMessage() {
        _saveMessage.value = null
    }

    fun saveAppointment() {
        val patientId = _selectedPatientId.value ?: return
        val physioId = _selectedPhysioId.value ?: return
        val appointmentDate = _selectedAppointmentDate.value ?: return
        val startTime = _selectedStartTime.value ?: return

        val durationMinutes =
            clinicSettings.value?.appointmentDurationMinutes
                ?: run {
                    _saveMessage.value =
                        "Clinic settings are still loading. Please try again."
                    return
                }

        val endTime =
            calculateEndTime(
                startTime = startTime,
                durationMinutes = durationMinutes
            ) ?: run {
                _saveMessage.value = "Invalid appointment time."
                return
            }

        viewModelScope.launch {

            val conflictingAppointments =
                appointmentRepository.countConflictingAppointments(
                    physioId = physioId,
                    appointmentDate = appointmentDate,
                    startTime = startTime,
                    endTime = endTime
                )

            if (conflictingAppointments > 0) {
                _saveMessage.value =
                    "This physio is already booked for the selected time."
                return@launch
            }

            appointmentRepository.addAppointment(
                AppointmentEntity(
                    patientId = patientId,
                    physioId = physioId,
                    appointmentDate = appointmentDate,
                    startTime = startTime,
                    endTime = endTime,
                    status = "SCHEDULED",
                    notes = null
                )
            )

            _saveMessage.value = "Appointment saved successfully."

            // Clear selections after successful save
            _selectedPatientId.value = null
            _selectedPhysioId.value = null
            _selectedAppointmentDate.value = null
            _selectedStartTime.value = null
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            appointmentRepository.cancelAppointment(appointmentId)
        }
    }

    fun updateAppointmentStatus(
        appointmentId: Long,
        status: String
    ) {
        viewModelScope.launch {
            appointmentRepository.updateAppointmentStatus(
                appointmentId = appointmentId,
                status = status
            )
        }
    }

    fun startEditingAppointment(
        appointment: AppointmentEntity
    ) {
        editingAppointmentStatus = appointment.status
        _editingAppointmentId.value = appointment.id

        selectPatient(appointment.patientId)
        selectPhysio(appointment.physioId)
        selectAppointmentDate(appointment.appointmentDate)
        selectStartTime(appointment.startTime)
    }

    fun cancelEditing() {
        _editingAppointmentId.value = null

        _selectedPatientId.value = null
        _selectedPhysioId.value = null
        _selectedAppointmentDate.value = null
        _selectedStartTime.value = null
    }

    fun updateExistingAppointment() {

        val appointmentId = _editingAppointmentId.value
            ?: return

        val patientId = _selectedPatientId.value
            ?: return

        val physioId = _selectedPhysioId.value
            ?: return

        val appointmentDate = _selectedAppointmentDate.value
            ?: return

        val startTime = _selectedStartTime.value
            ?: return

        val durationMinutes =
            clinicSettings.value?.appointmentDurationMinutes
                ?: run {
                    _saveMessage.value =
                        "Clinic settings are still loading. Please try again."
                    return
                }

        val endTime =
            calculateEndTime(
                startTime = startTime,
                durationMinutes = durationMinutes
            ) ?: run {
                _saveMessage.value = "Invalid appointment time."
                return
            }

        viewModelScope.launch {

            val conflictCount =
                appointmentRepository.countConflictingAppointmentsForUpdate(
                    appointmentId = appointmentId,
                    physioId = physioId,
                    appointmentDate = appointmentDate,
                    startTime = startTime,
                    endTime = endTime
                )

            if (conflictCount > 0) {
                _saveMessage.value =
                    "This physio is already booked for the selected time."
                return@launch
            }

            appointmentRepository.updateAppointment(
                appointmentId = appointmentId,
                patientId = patientId,
                physioId = physioId,
                appointmentDate = appointmentDate,
                startTime = startTime,
                endTime = endTime,
                status = editingAppointmentStatus,
                notes = null
            )

            _saveMessage.value =
                "Appointment updated successfully."

            _editingAppointmentId.value = null

            _selectedPatientId.value = null
            _selectedPhysioId.value = null
            _selectedAppointmentDate.value = null
            _selectedStartTime.value = null
        }
    }



}