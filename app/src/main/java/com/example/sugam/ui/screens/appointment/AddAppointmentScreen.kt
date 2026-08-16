package com.example.sugam.ui.screens.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentScreen(
    viewModel: AppointmentViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val patients by viewModel.patients.collectAsState(initial = emptyList())
    val physios by viewModel.physios.collectAsState()
    val selectedPatientId by viewModel.selectedPatientId.collectAsState()
    val selectedPhysioId by viewModel.selectedPhysioId.collectAsState()
    val selectedDate by viewModel.selectedAppointmentDate.collectAsState()
    val selectedStartTime by viewModel.selectedStartTime.collectAsState()
    val saveMessage by viewModel.saveMessage.collectAsState()
    val editingAppointmentId by viewModel.editingAppointmentId.collectAsState()

    val selectedPatient = patients.find { it.id == selectedPatientId }
    val selectedPhysio = physios.find { it.id == selectedPhysioId }

    var expandedPatients by remember { mutableStateOf(false) }
    var expandedPhysios by remember { mutableStateOf(false) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(saveMessage) {
        if (saveMessage == "Appointment saved successfully." || 
            saveMessage == "Appointment updated successfully.") {
            onSaveSuccess()
            viewModel.clearSaveMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (editingAppointmentId == null) "Add New Appointment" 
                        else "Edit Appointment",
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPatients,
                onExpandedChange = { expandedPatients = it }
            ) {
                OutlinedTextField(
                    value = selectedPatient?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Patients") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPatients) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )
                ExposedDropdownMenu(
                    expanded = expandedPatients,
                    onDismissRequest = { expandedPatients = false }
                ) {
                    patients.forEach { patient ->
                        DropdownMenuItem(
                            text = { Text(patient.name) },
                            onClick = {
                                viewModel.selectPatient(patient.id)
                                expandedPatients = false
                            }
                        )
                    }
                }
            }

            // Physio Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPhysios,
                onExpandedChange = { expandedPhysios = it }
            ) {
                OutlinedTextField(
                    value = selectedPhysio?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Doctor") }, // As per screenshot
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPhysios) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPhysios,
                    onDismissRequest = { expandedPhysios = false }
                ) {
                    physios.forEach { physio ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    physio.name + if (physio.active) "" else " (Deactivated)"
                                ) 
                            },
                            onClick = {
                                viewModel.selectPhysio(physio.id)
                                expandedPhysios = false
                            }
                        )
                    }
                }
            }

            // Phone Number
            OutlinedTextField(
                value = selectedPatient?.phone ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            // Date and Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = selectedDate ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )
                OutlinedTextField(
                    value = selectedStartTime ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )
            }

            // Slots and Repeat Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = "00:20:00", // Placeholder as per screenshot
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Slots :") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                )
                OutlinedTextField(
                    value = "0", // Placeholder as per screenshot
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Repeat :") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                )
            }

            // Notes
            val appointmentNotes by viewModel.notes.collectAsState()
            OutlinedTextField(
                value = appointmentNotes ?: "",
                onValueChange = { viewModel.updateNotes(it) },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    if (editingAppointmentId == null) {
                        viewModel.saveAppointment()
                    } else {
                        viewModel.updateExistingAppointment()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Submit", style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE)
                        viewModel.selectAppointmentDate(date)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(is24Hour = true)
        TimePickerDialog(
            title = { Text("Select Time") },
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    viewModel.selectStartTime(time)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    if (saveMessage != null && 
        saveMessage != "Appointment saved successfully." && 
        saveMessage != "Appointment updated successfully.") {
        AlertDialog(
            onDismissRequest = { viewModel.clearSaveMessage() },
            text = { Text(saveMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearSaveMessage() }) { Text("OK") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier.width(IntrinsicSize.Min).height(IntrinsicSize.Min).background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                title()
                Spacer(modifier = Modifier.height(20.dp))
                content()
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    dismissButton()
                    confirmButton()
                }
            }
        }
    }
}
