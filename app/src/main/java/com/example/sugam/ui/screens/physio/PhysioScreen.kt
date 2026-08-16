package com.example.sugam.ui.screens.physio

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sugam.data.local.entity.PhysioEntity
import androidx.compose.material3.AlertDialog

@Composable
fun PhysioScreen(
    physioRepository: com.example.sugam.data.repository.PhysioRepository,
    modifier: Modifier = Modifier
) {
    val viewModel: PhysioViewModel = viewModel(
        factory = PhysioViewModelFactory(physioRepository)
    )

    val physios by viewModel.physios.collectAsState(initial = emptyList())

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var editingPhysioId by remember { mutableStateOf<Long?>(null) }
    var physioToDelete by remember { mutableStateOf<PhysioEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (editingPhysioId == null) {
                "Add Physio"
            } else {
                "Edit Physio"
            },
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = specialization,
            onValueChange = { specialization = it },
            label = { Text("Specialization") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {

                    if (editingPhysioId == null) {
                        // Add new physio
                        viewModel.addPhysio(
                            PhysioEntity(
                                name = name.trim(),
                                phone = phone.trim().ifBlank { null },
                                specialization = specialization.trim().ifBlank { null }
                            )
                        )
                    } else {
                        // Update existing physio
                        viewModel.updatePhysio(
                            PhysioEntity(
                                id = editingPhysioId!!,
                                name = name.trim(),
                                phone = phone.trim().ifBlank { null },
                                specialization = specialization.trim().ifBlank { null }
                            )
                        )
                    }

                    // Reset form
                    editingPhysioId = null
                    name = ""
                    phone = ""
                    specialization = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (editingPhysioId == null) {
                    "Add Physio"
                } else {
                    "Update Physio"
                }
            )
        }

        if (editingPhysioId != null) {
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    editingPhysioId = null
                    name = ""
                    phone = ""
                    specialization = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Physiotherapists",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (physios.isEmpty()) {
                item {
                    com.example.sugam.ui.screens.EmptyState(
                        message = "No physiotherapists added yet."
                    )
                }
            } else {
                items(physios) { physio ->
                    PhysioCard(
                        physio = physio,
                        onEdit = {
                            editingPhysioId = physio.id
                            name = physio.name
                            phone = physio.phone.orEmpty()
                            specialization = physio.specialization.orEmpty()
                        },
                        onDelete = {
                            physioToDelete = physio
                        },
                        onToggleActive = {
                            viewModel.setPhysioActive(
                                physio,
                                !physio.active
                            )
                        }
                    )
                }
            }
        }

        if (physioToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    physioToDelete = null
                },
                title = {
                    Text("Delete Physio?")
                },
                text = {
                    Text(
                        "Are you sure you want to delete " +
                                "${physioToDelete!!.name}?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deletePhysio(physioToDelete!!)
                            physioToDelete = null
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            physioToDelete = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }


    }
}

@Composable
private fun PhysioCard(
    physio: PhysioEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = physio.name,
                style = MaterialTheme.typography.titleMedium
            )

            physio.phone?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            physio.specialization?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (physio.active) "Active" else "Inactive",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onEdit
                ) {
                    Text("Edit")
                }

                TextButton(
                    onClick = onToggleActive
                ) {
                    Text(
                        if (physio.active) {
                            "Deactivate"
                        } else {
                            "Activate"
                        }
                    )
                }

                TextButton(
                    onClick = onDelete
                ) {
                    Text("Delete")
                }
            }
        }

    }
}