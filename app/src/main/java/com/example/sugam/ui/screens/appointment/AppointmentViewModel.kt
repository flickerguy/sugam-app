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
            .flatMapLatest { settings ->
                if (settings == null) {
                    // Provide a default fallback if the DB is empty
                    kotlinx.coroutines.flow.flowOf(com.example.sugam.data.local.entity.ClinicSettingsEntity())
                } else {
                    kotlinx.coroutines.flow.flowOf(settings)
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                com.example.sugam.data.local.entity.ClinicSettingsEntity()
            )

    private val _selectedPatientId = MutableStateFlow<Long?>(null)

    val selectedPatientId: StateFlow<Long?> = _selectedPatientId

    fun selectPatient(patientId: Long) {
        _selectedPatientId.value = patientId
    }

    val physios: StateFlow<List<PhysioEntity>> =
        physioRepository.getAllPhysios()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

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

    private val _notes = MutableStateFlow<String?>(null)
    val notes: StateFlow<String?> = _notes

    fun updateNotes(notes: String?) {
        _notes.value = notes
    }

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

    private fun validateAppointment(): Boolean {
        if (_selectedPatientId.value == null) {
            _saveMessage.value = "Please select a patient."
            return false
        }
        if (_selectedPhysioId.value == null) {
            _saveMessage.value = "Please select a physio."
            return false
        }
        if (_selectedAppointmentDate.value == null) {
            _saveMessage.value = "Please select an appointment date."
            return false
        }
        if (_selectedStartTime.value == null) {
            _saveMessage.value = "Please select a start time."
            return false
        }

        val physio = physios.value.find { it.id == _selectedPhysioId.value }
        if (physio == null || !physio.active) {
            _saveMessage.value = "The selected physio is currently inactive."
            return false
        }

        val settings = clinicSettings.value

        // Working hour validation
        try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val start = LocalTime.parse(_selectedStartTime.value!!, formatter)
            
            val duration = settings.appointmentDurationMinutes
            val end = start.plusMinutes(duration.toLong())
            
            val workingStart = LocalTime.parse(settings.workingStartTime, formatter)
            val workingEnd = LocalTime.parse(settings.workingEndTime, formatter)

            if (start.isBefore(workingStart) || end.isAfter(workingEnd)) {
                _saveMessage.value = "Appointment must be within working hours (${settings.workingStartTime} - ${settings.workingEndTime})."
                return false
            }
        } catch (e: Exception) {
            _saveMessage.value = "Invalid time format in settings or selection."
            return false
        }

        return true
    }

    fun saveAppointment() {
        if (!validateAppointment()) return

        val patientId = _selectedPatientId.value!!
        val physioId = _selectedPhysioId.value!!
        val appointmentDate = _selectedAppointmentDate.value!!
        val startTime = _selectedStartTime.value!!
        val settings = clinicSettings.value

        val endTime =
            calculateEndTime(
                startTime = startTime,
                durationMinutes = settings.appointmentDurationMinutes
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
                    notes = _notes.value
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
        updateNotes(appointment.notes)
    }

    fun resetForm() {
        _editingAppointmentId.value = null
        _selectedPatientId.value = null
        _selectedPhysioId.value = null
        _selectedAppointmentDate.value = null
        _selectedStartTime.value = null
        _notes.value = null
        _saveMessage.value = null
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

        if (!validateAppointment()) return

        val patientId = _selectedPatientId.value!!
        val physioId = _selectedPhysioId.value!!
        val appointmentDate = _selectedAppointmentDate.value!!
        val startTime = _selectedStartTime.value!!
        val settings = clinicSettings.value

        val endTime =
            calculateEndTime(
                startTime = startTime,
                durationMinutes = settings.appointmentDurationMinutes
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
                notes = _notes.value
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