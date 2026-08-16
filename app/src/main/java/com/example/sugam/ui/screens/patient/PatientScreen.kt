package com.example.sugam.ui.screens.patient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sugam.data.local.entity.PatientEntity
import androidx.compose.material3.TextButton
import androidx.compose.foundation.clickable
import com.example.sugam.data.local.entity.AppointmentEntity
import com.example.sugam.data.local.model.PatientAppointmentHistory


@Composable
fun PatientScreen(
    viewModel: PatientViewModel,
    onNavigateToDetails: (Long) -> Unit
) {
    val patients by viewModel.patients.collectAsState(initial = emptyList())

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Patients",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Patient name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.addPatient(
                    PatientEntity(
                        name = name,
                        phone = phone,
                        age = age.toIntOrNull()
                    )
                )

                name = ""
                phone = ""
                age = ""
            },
            enabled = name.isNotBlank() && phone.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Patient")
        }

        Text(
            text = "Saved Patients",
            style = MaterialTheme.typography.titleLarge
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(patients) { patient ->
                PatientCard(
                    patient = patient,
                    onClick = {
                        onNavigateToDetails(patient.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun PatientCard(
    patient: PatientEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = patient.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = patient.phone,
                    style = MaterialTheme.typography.bodyMedium
                )

                patient.age?.let {
                    Text(
                        text = "Age: $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

