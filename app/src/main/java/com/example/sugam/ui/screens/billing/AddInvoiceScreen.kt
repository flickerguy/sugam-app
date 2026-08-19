package com.example.sugam.ui.screens.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvoiceScreen(
    viewModel: BillingViewModel,
    patientId: Long,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val invoiceDate by viewModel.invoiceDate.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()
    
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Billing", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                            .background(Color(0xFFD32F2F), RoundedCornerShape(4.dp))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF03A9F4))
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "Total Amount: %.2f", totalAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { 
                                viewModel.saveInvoice(patientId, onSaveSuccess)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Save invoice (${items.size})")
                        }
                        
                        TextButton(onClick = { viewModel.addItem() }) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF03A9F4))
                            Text("ADD", color = Color(0xFF03A9F4))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Invoice Date
            OutlinedTextField(
                value = invoiceDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Invoice Date") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    InvoiceItemCard(
                        item = item,
                        index = items.indexOf(item) + 1,
                        onUpdate = { viewModel.updateItem(it) },
                        onDelete = { viewModel.removeItem(item.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
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
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        viewModel.updateDate(date)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoiceItemCard(
    item: InvoiceItem,
    index: Int,
    onUpdate: (InvoiceItem) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Sl.No $index", style = MaterialTheme.typography.labelMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Service Dropdown Placeholder
            var expandedService by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedService,
                onExpandedChange = { expandedService = it }
            ) {
                OutlinedTextField(
                    value = item.service,
                    onValueChange = { onUpdate(item.copy(service = it)) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedService) },
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedService,
                    onDismissRequest = { expandedService = false }
                ) {
                    DropdownMenuItem(text = { Text("Consultation") }, onClick = { onUpdate(item.copy(service = "Consultation")); expandedService = false })
                    DropdownMenuItem(text = { Text("Therapy Session") }, onClick = { onUpdate(item.copy(service = "Therapy Session")); expandedService = false })
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = item.quantity,
                    onValueChange = { onUpdate(item.copy(quantity = it)) },
                    label = { Text("Quantity") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = item.unitPrice,
                    onValueChange = { onUpdate(item.copy(unitPrice = it)) },
                    label = { Text("Unit Price") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = item.discount,
                    onValueChange = { onUpdate(item.copy(discount = it)) },
                    label = { Text("Discount") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("%", color = if (item.isPercentageDiscount) Color(0xFF4CAF50) else Color.Gray, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = !item.isPercentageDiscount,
                        onCheckedChange = { onUpdate(item.copy(isPercentageDiscount = !it)) },
                        modifier = Modifier.scale(0.8f)
                    )
                    Text("₹", color = if (!item.isPercentageDiscount) Color(0xFF4CAF50) else Color.Gray, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Category Dropdown Placeholder
                OutlinedTextField(
                    value = item.category,
                    onValueChange = { onUpdate(item.copy(category = it)) },
                    modifier = Modifier.weight(1.5f),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    shape = RoundedCornerShape(8.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text("Total", style = MaterialTheme.typography.labelSmall)
                    OutlinedTextField(
                        value = String.format(Locale.getDefault(), "%.0f", item.total),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

