package com.example.sugam.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sugam.domain.model.Appointment
import androidx.compose.runtime.collectAsState
import com.example.sugam.ui.screens.dashboard.DashboardViewModel
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.data.repository.PatientRepository
import com.example.sugam.data.repository.PhysioRepository
import com.example.sugam.ui.screens.dashboard.DashboardViewModelFactory

data class DashboardAppointment(
    val time: String,
    val patientName: String,
    val physioName: String
)

@Composable
fun DashboardScreen(
    appointmentRepository: AppointmentRepository,
    patientRepository: PatientRepository,
    physioRepository: PhysioRepository,
    onNavigateToPhysios: () -> Unit,
    onCreateAppointment: () -> Unit
) {
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(
            appointmentRepository,
            patientRepository,
            physioRepository
        )
    )

    val appointments = viewModel.appointments.collectAsState(
        initial = emptyList()
    )

    val scheduledCount = appointments.value.count {
        it.status == "SCHEDULED"
    }

    val confirmedCount = appointments.value.count {
        it.status == "CONFIRMED"
    }

    val completedCount = appointments.value.count {
        it.status == "COMPLETED"
    }

    val cancelledCount = appointments.value.count {
        it.status == "CANCELLED"
    }

    val noShowCount = appointments.value.count {
        it.status == "NO_SHOW"
    }

    val upcomingAppointments = viewModel.upcomingAppointments.collectAsState(
        initial = emptyList()
    )


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                text = "Sugam",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Today's overview",
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = {
                    onNavigateToPhysios()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Physios")
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardSummaryCard(
                    title = "Appointments",
                    value = appointments.value.size.toString(),
                    modifier = Modifier.weight(1f)
                )

                DashboardSummaryCard(
                    title = "Scheduled",
                    value = scheduledCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardSummaryCard(
                    title = "Confirmed",
                    value = confirmedCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                DashboardSummaryCard(
                    title = "Completed",
                    value = completedCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardSummaryCard(
                    title = "Cancelled",
                    value = cancelledCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                DashboardSummaryCard(
                    title = "No-show",
                    value = noShowCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Upcoming Appointments",
                style = MaterialTheme.typography.titleLarge
            )

            if (upcomingAppointments.value.isEmpty()) {
                Text(
                    text = "No upcoming appointments.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(upcomingAppointments.value) { appointment ->
            AppointmentSummaryCard(appointment)
        }

        item {
            Button(
                onClick = onCreateAppointment,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Appointment")
            }
        }

    }
}

@Composable
private fun DashboardSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun AppointmentSummaryCard(
    appointment: Appointment
){
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = appointment.startTime
            )

            Text(
                text = appointment.patientName
            )

            Text(
                text = appointment.physioName
            )
        }
    }
}