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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.example.sugam.ui.screens.CenteredScreenText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    viewModel: AppointmentViewModel,
    onAddAppointment: () -> Unit,
    onEditAppointment: (AppointmentEntity) -> Unit
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAppointment,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Appointment")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    EmptyState(
                        message = "No appointments found for $viewDate."
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
                            onEditAppointment(appointment)
                        }
                    )
                }
            }
        }
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

            if (!physio.active) {
                Text(
                    text = "DEACTIVATED",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
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