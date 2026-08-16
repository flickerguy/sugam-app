package com.example.sugam.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sugam.domain.model.Appointment
import com.example.sugam.ui.screens.EmptyState
import androidx.compose.material.icons.automirrored.filled.Chat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onEditAppointment: (Long) -> Unit
) {
    val appointments by viewModel.appointments.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val todayCount by viewModel.todayCount.collectAsState()
    val upcomingCount by viewModel.upcomingCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "PAPPYJOE",
                color = Color(0xFF03A9F4),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "SUGAM PHYSIOTHERAPY CLINIC",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Appointment Search Here..", fontSize = 14.sp) },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                tonalElevation = 2.dp,
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.FilterList, 
                        contentDescription = "Filter",
                        tint = Color(0xFF03A9F4)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                count = todayCount,
                label = "Today's\nAppointments",
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                count = upcomingCount,
                label = "Upcoming\nAppointments",
                containerColor = Color.White,
                contentColor = Color.Gray,
                labelColor = Color.Gray,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Appointment List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (appointments.isEmpty()) {
                item {
                    EmptyState(message = "No appointments found.")
                }
            } else {
                items(appointments) { appointment ->
                    ModernAppointmentCard(
                        appointment = appointment,
                        onCancel = { viewModel.cancelAppointment(appointment.id) },
                        onEdit = { onEditAppointment(appointment.id) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SummaryCard(
    count: Int,
    label: String,
    containerColor: Color,
    contentColor: Color,
    labelColor: Color = contentColor,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = count.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = labelColor,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun ModernAppointmentCard(
    appointment: Appointment,
    onCancel: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.status.lowercase().replaceFirstChar { it.uppercase() },
                    color = Color(0xFF03A9F4),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                
                if (appointment.status != "CANCELLED") {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350))
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Patient Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE9ECEF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text("Patient", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        appointment.patientName, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionIcon(Icons.AutoMirrored.Filled.Chat, Color(0xFF4CAF50))
                    ActionIcon(Icons.Default.VideoCall, Color(0xFF03A9F4), filled = true)
                    ActionIcon(Icons.Default.Call, Color(0xFFE9ECEF), tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date & Time Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F9FF), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, size = 16.dp, tint = Color(0xFF03A9F4))
                Spacer(modifier = Modifier.width(8.dp))
                Text(appointment.appointmentDate, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                
                Spacer(modifier = Modifier.width(16.dp))
                Divider(modifier = Modifier.width(1.dp).height(16.dp), color = Color.LightGray)
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(Icons.Default.AccessTime, contentDescription = null, size = 16.dp, tint = Color(0xFF03A9F4))
                Spacer(modifier = Modifier.width(8.dp))
                Text(appointment.startTime, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Doctor Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F9FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, size = 16.dp, tint = Color(0xFF03A9F4))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("DOCTOR", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(appointment.physioName, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = Color(0xFFE9ECEF),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        appointment.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionIcon(icon: ImageVector, color: Color, filled: Boolean = false, tint: Color = Color.White) {
    Surface(
        modifier = Modifier.size(36.dp),
        shape = CircleShape,
        color = if (filled) color else Color(0xFFF8F9FA),
        border = if (!filled) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9ECEF)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon, 
                contentDescription = null, 
                modifier = Modifier.size(20.dp),
                tint = if (filled) tint else color
            )
        }
    }
}

// Fixed Icon size parameter workaround
@Composable
fun Icon(imageVector: ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: Color = LocalContentColor.current) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        tint = tint
    )
}
