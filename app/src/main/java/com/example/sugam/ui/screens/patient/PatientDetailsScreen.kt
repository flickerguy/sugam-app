package com.example.sugam.ui.screens.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sugam.data.local.entity.PatientEntity
import com.example.sugam.data.local.model.PatientAppointmentHistory

@Composable
fun PatientDetailsScreen(
    viewModel: PatientViewModel,
    patientId: Long,
    onBack: () -> Unit
) {
    LaunchedEffect(patientId) {
        viewModel.setPatientId(patientId)
    }

    val patient by viewModel.selectedPatient.collectAsState()
    val history by viewModel.patientAppointments.collectAsState()

    Scaffold(
        topBar = {
            Button(onClick = onBack, modifier = Modifier.padding(8.dp)) {
                Text("Back")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            patient?.let { p ->
                PatientInfoCard(p)
            }

            Text(
                text = "Appointment History",
                style = MaterialTheme.typography.titleLarge
            )

            if (history.isEmpty()) {
                com.example.sugam.ui.screens.EmptyState(
                    message = "No appointments found."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(history) { appointment ->
                        AppointmentHistoryCard(appointment)
                    }
                }
            }
        }
    }
}

@Composable
private fun PatientInfoCard(patient: PatientEntity) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = patient.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(text = "Phone: ${patient.phone}")
            patient.age?.let {
                Text(text = "Age: $it")
            }
            patient.address?.let {
                Text(text = "Address: $it")
            }
            patient.notes?.let {
                Text(text = "Notes: $it")
            }
        }
    }
}

@Composable
private fun AppointmentHistoryCard(
    appointment: PatientAppointmentHistory
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${appointment.appointmentDate}  ${appointment.startTime} - ${appointment.endTime}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Physio: ${appointment.physioName}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Status: ${appointment.status}",
                style = MaterialTheme.typography.bodyMedium
            )

            appointment.notes?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = "Notes: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
