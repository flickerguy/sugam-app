package com.example.sugam.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sugam.ui.screens.settings.ClinicSettingsViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ClinicSettingsViewModel
) {
    val settings by viewModel.settings.collectAsState()

    if (settings == null) {
        CenteredScreenText("Loading settings...")
        return
    }

    val currentSettings = settings!!
    var clinicName by remember { mutableStateOf("") }
    var workingStartTime by remember { mutableStateOf("") }
    var workingEndTime by remember { mutableStateOf("") }
    var appointmentDuration by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val context = LocalContext.current
    var durationExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(currentSettings.id) {
        clinicName = currentSettings.clinicName
        workingStartTime = currentSettings.workingStartTime
        workingEndTime = currentSettings.workingEndTime
        appointmentDuration =
            currentSettings.appointmentDurationMinutes.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Clinic Settings",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = clinicName,
            onValueChange = { clinicName = it },
            label = { Text("Clinic Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val current = runCatching {
                        LocalTime.parse(
                            workingStartTime,
                            timeFormatter
                        )
                    }.getOrDefault(
                        LocalTime.of(9, 0)
                    )

                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            workingStartTime =
                                LocalTime.of(hour, minute)
                                    .format(timeFormatter)
                        },
                        current.hour,
                        current.minute,
                        true
                    ).show()
                }
        ) {
            OutlinedTextField(
                value = workingStartTime,
                onValueChange = {},
                label = {
                    Text("Working Start Time")
                },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val current = runCatching {
                        LocalTime.parse(
                            workingEndTime,
                            timeFormatter
                        )
                    }.getOrDefault(
                        LocalTime.of(19, 0)
                    )

                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            workingEndTime =
                                LocalTime.of(hour, minute)
                                    .format(timeFormatter)
                        },
                        current.hour,
                        current.minute,
                        true
                    ).show()
                }
        ) {
            OutlinedTextField(
                value = workingEndTime,
                onValueChange = {},
                label = {
                    Text("Working End Time")
                },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false
            )
        }



        var durationExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = durationExpanded,
            onExpandedChange = {
                durationExpanded = !durationExpanded
            }
        ) {
            OutlinedTextField(
                value = "$appointmentDuration minutes",
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Appointment Duration")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = durationExpanded,
                onDismissRequest = {
                    durationExpanded = false
                }
            ) {
                listOf(15, 30, 45, 60).forEach { duration ->
                    DropdownMenuItem(
                        text = {
                            Text("$duration minutes")
                        },
                        onClick = {
                            appointmentDuration = duration.toString()
                            durationExpanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                val start = runCatching {
                    LocalTime.parse(
                        workingStartTime.trim(),
                        DateTimeFormatter.ofPattern("HH:mm")
                    )
                }.getOrNull()

                val end = runCatching {
                    LocalTime.parse(
                        workingEndTime.trim(),
                        DateTimeFormatter.ofPattern("HH:mm")
                    )
                }.getOrNull()

                val duration = appointmentDuration.toIntOrNull()

                errorMessage = when {
                    clinicName.isBlank() ->
                        "Clinic name is required."

                    start == null ->
                        "Enter a valid start time (HH:mm)."

                    end == null ->
                        "Enter a valid end time (HH:mm)."

                    !end.isAfter(start) ->
                        "Working end time must be after start time."

                    duration == null || duration <= 0 ->
                        "Appointment duration must be greater than 0."

                    else -> null
                }

                if (errorMessage == null) {
                    viewModel.updateSettings(
                        clinicName = clinicName.trim(),
                        workingStartTime = workingStartTime.trim(),
                        workingEndTime = workingEndTime.trim(),
                        appointmentDurationMinutes = duration!!
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}