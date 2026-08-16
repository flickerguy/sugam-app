package com.example.sugam.ui.screens

import android.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sugam.data.local.entity.PatientEntity
import com.example.sugam.data.repository.PatientRepository
import com.example.sugam.ui.screens.appointment.AppointmentViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.sugam.data.local.entity.PhysioEntity
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.setValue
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.sugam.data.local.entity.AppointmentEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    viewModel: AppointmentViewModel
) {
    val patients = viewModel.patients
        .collectAsState(initial = emptyList())
    val selectedPatientId by viewModel.selectedPatientId.collectAsState()
    val physios = viewModel.physios
        .collectAsState(initial = emptyList())

    val selectedPhysioId by viewModel.selectedPhysioId.collectAsState()

    val clinicSettings by viewModel.clinicSettings.collectAsState()

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val selectedAppointmentDate by
    viewModel.selectedAppointmentDate.collectAsState()

    val selectedStartTime by
    viewModel.selectedStartTime.collectAsState()

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    val selectedEndTime = selectedStartTime?.let {
        viewModel.calculateEndTime(
            startTime = it,
            durationMinutes =
                clinicSettings?.appointmentDurationMinutes ?: 30
        )
    }

    val selectedPatient = patients.value
        .firstOrNull { it.id == selectedPatientId }

    val selectedPhysio = physios.value
        .firstOrNull { it.id == selectedPhysioId }

    val saveMessage by viewModel.saveMessage.collectAsState()

    val appointments by viewModel.appointments.collectAsState(
        initial = emptyList()
    )

    var showViewDatePicker by remember {
        mutableStateOf(false)
    }

    val viewDate by viewModel.viewDate.collectAsState()

    var appointmentToCancel by remember {
        mutableStateOf<Long?>(null)
    }

    val editingAppointmentId by
    viewModel.editingAppointmentId.collectAsState()

    fun patientName(patientId: Long): String {
        return patients.value
            .firstOrNull { it.id == patientId }
            ?.name
            ?: "Unknown Patient"
    }

    fun physioName(physioId: Long): String {
        return physios.value
            .firstOrNull { it.id == physioId }
            ?.name
            ?: "Unknown Physio"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Button(
                onClick = {
                    showViewDatePicker = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Date: $viewDate"
                )
            }
        }

        item {
            Text(
                text = "Appointments",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (appointments.isEmpty()) {
            item {
                Text(
                    text = "No appointments found."
                )
            }
        } else {
            items(
                items = appointments,
                key = { it.id }
            ) { appointment ->

                AppointmentListCard(
                    appointment = appointment,
                    patientName = patientName(appointment.patientId),
                    physioName = physioName(appointment.physioId),
                    onCancel = {
                        appointmentToCancel = appointment.id
                    },
                    onStatusChange = { status ->
                        viewModel.updateAppointmentStatus(
                            appointmentId = appointment.id,
                            status = status
                        )
                    },
                    onEdit = {
                        viewModel.startEditingAppointment(appointment)
                    }
                )
            }
        }

        item {
            Text(
                text = if (editingAppointmentId != null) {
                    "Edit Appointment"
                } else {
                    "Create Appointment"
                },
                style = MaterialTheme.typography.headlineMedium
            )
        }

        if (editingAppointmentId != null) {
            item {
                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        viewModel.cancelEditing()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel Edit")
                }
            }
        }

        if (selectedPatientId != null) {
            item {
                Text(
                    text = "Selected Patient ID: $selectedPatientId",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        item {
            Text(
                text = "Select Patient",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(patients.value) { patient ->
            PatientSelectionCard(
                patient = patient,
                selected = patient.id == selectedPatientId,
                onSelect = {
                    viewModel.selectPatient(patient.id)
                }
            )
        }

        item {
            Text(
                text = "Select Physio",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(physios.value) { physio ->
            PhysioSelectionCard(
                physio = physio,
                selected = physio.id == selectedPhysioId,
                onSelect = {
                    viewModel.selectPhysio(physio.id)
                }
            )
        }

        item {
            Text(
                text = "Select Date",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            Button(
                onClick = {
                    showDatePicker = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedAppointmentDate
                        ?: "Choose Appointment Date"
                )
            }
        }

        item {
            Text(
                text = "Select Time",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            Button(
                onClick = {
                    showTimePicker = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedStartTime
                        ?: "Choose Start Time"
                )
            }
        }

        if (
            selectedPatientId != null &&
            selectedPhysioId != null &&
            selectedAppointmentDate != null &&
            selectedStartTime != null
        ) {
            item {
                Text(
                    text = "Appointment Summary",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Patient: ${selectedPatient?.name ?: ""}"
                        )

                        Text(
                            text = "Physio: ${selectedPhysio?.name ?: ""}"
                        )

                        Text(
                            text = "Date: $selectedAppointmentDate"
                        )

                        Text(
                            text = "Time: $selectedStartTime - $selectedEndTime"
                        )
                    }
                }
            }


            item {
                Button(
                    onClick = {
                        if (editingAppointmentId != null) {
                            viewModel.updateExistingAppointment()
                        } else {
                            viewModel.saveAppointment()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (editingAppointmentId != null) {
                            "Save Changes"
                        } else {
                            "Save Appointment"
                        }
                    )
                }
            }
        }


    }

    if (appointmentToCancel != null) {
        AlertDialog(
            onDismissRequest = {
                appointmentToCancel = null
            },
            title = {
                Text("Cancel Appointment")
            },
            text = {
                Text(
                    "Are you sure you want to cancel this appointment?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        appointmentToCancel?.let { id ->
                            viewModel.cancelAppointment(id)
                        }

                        appointmentToCancel = null
                    }
                ) {
                    Text("Cancel Appointment")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        appointmentToCancel = null
                    }
                ) {
                    Text("Keep Appointment")
                }
            }
        )
    }

    if (showViewDatePicker) {

        val viewDatePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                showViewDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewDatePickerState.selectedDateMillis?.let { millis ->

                            val date = Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                                .format(DateTimeFormatter.ISO_LOCAL_DATE)

                            viewModel.selectViewDate(date)
                        }

                        showViewDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showViewDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = viewDatePickerState
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->

                            val date = Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                                .format(
                                    DateTimeFormatter.ISO_LOCAL_DATE
                                )

                            viewModel.selectAppointmentDate(date)
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    if (showTimePicker) {

        val timePickerState = rememberTimePickerState(
            initialHour = 9,
            initialMinute = 0,
            is24Hour = true
        )

        TimePickerDialog(
            title = {
                Text("Select Start Time")
            },
            onDismissRequest = {
                showTimePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val time = String.format(
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )

                        viewModel.selectStartTime(time)

                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(
                state = timePickerState
            )
        }
    }

    if (saveMessage != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearSaveMessage()
            },
            text = {
                Text(saveMessage!!)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearSaveMessage()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

}

@Composable
private fun PatientSelectionCard(
    patient: PatientEntity,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = patient.name,
                style = MaterialTheme.typography.titleMedium
            )

            patient.phone?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (selected) "Selected" else "Select"
                )
            }

            if (selected) {
                Text(
                    text = "Selected patient",
                    style = MaterialTheme.typography.bodyMedium
                )
            }


        }
    }
}

@Composable
private fun PhysioSelectionCard(
    physio: PhysioEntity,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = physio.name,
                style = MaterialTheme.typography.titleMedium
            )

            physio.specialization?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (selected) "Selected" else "Select"
                )
            }
        }
    }
}

@Composable
private fun AppointmentListCard(
    appointment: AppointmentEntity,
    patientName: String,
    physioName: String,
    onCancel: () -> Unit,
    onStatusChange: (String) -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "$patientName",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Physio: $physioName"
            )

            Text(
                text = "Date: ${appointment.appointmentDate}"
            )

            Text(
                text = "Time: ${appointment.startTime} - ${appointment.endTime}"
            )

            Text(
                text = "Status: ${appointment.status}"
            )

            if (
                appointment.status == "SCHEDULED" ||
                appointment.status == "CONFIRMED"
            ) {
                TextButton(
                    onClick = onEdit
                ) {
                    Text("Edit")
                }
            }

            when (appointment.status) {

                "SCHEDULED" -> {
                    TextButton(
                        onClick = {
                            onStatusChange("CONFIRMED")
                        }
                    ) {
                        Text("Confirm")
                    }

                    TextButton(
                        onClick = {
                            onCancel()
                        }
                    ) {
                        Text("Cancel")
                    }
                }

                "CONFIRMED" -> {
                    TextButton(
                        onClick = {
                            onStatusChange("COMPLETED")
                        }
                    ) {
                        Text("Complete")
                    }

                    TextButton(
                        onClick = {
                            onStatusChange("NO_SHOW")
                        }
                    ) {
                        Text("No Show")
                    }

                    TextButton(
                        onClick = {
                            onCancel()
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            }

        }
    }
}