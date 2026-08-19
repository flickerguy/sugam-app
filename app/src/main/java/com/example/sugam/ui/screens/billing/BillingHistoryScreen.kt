package com.example.sugam.ui.screens.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sugam.data.local.entity.PatientEntity
import com.example.sugam.data.local.entity.InvoiceEntity
import com.example.sugam.ui.screens.patient.PatientViewModel
import androidx.compose.material.icons.automirrored.filled.Chat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingHistoryScreen(
    patientViewModel: PatientViewModel,
    billingViewModel: BillingViewModel,
    patientId: Long,
    onBack: () -> Unit,
    onAddInvoice: () -> Unit
) {
    LaunchedEffect(patientId) {
        patientViewModel.setPatientId(patientId)
    }

    val patient by patientViewModel.selectedPatient.collectAsState()
    val invoices by remember(patientId) { 
        billingViewModel.getInvoicesForPatient(patientId)
    }.collectAsState(initial = emptyList())
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Billing", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF03A9F4))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddInvoice,
                containerColor = Color(0xFF66BB6A),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Invoice")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {
            // Patient Info Header
            patient?.let { p ->
                PatientHeader(p)
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF03A9F4),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF03A9F4)
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Invoice", color = if (selectedTab == 0) Color(0xFF03A9F4) else Color.Gray) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Receipt", color = if (selectedTab == 1) Color(0xFF03A9F4) else Color.Gray) }
                )
            }

            if (selectedTab == 0) {
                InvoiceList(invoices)
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Receipts Found", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun PatientHeader(patient: PatientEntity) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFF03A9F4), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = patient.name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = patient.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Patient Id : SUG${patient.id} / Male / ${patient.age ?: "NA"}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Call, contentDescription = null, size = 16.dp, tint = Color.LightGray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Primary No.", fontSize = 14.sp, color = Color.Gray)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = patient.phone, fontWeight = FontWeight.Bold)
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, size = 24.dp, tint = Color(0xFF4CAF50))
                    Icon(Icons.Default.Call, contentDescription = null, size = 24.dp, tint = Color(0xFF03A9F4))
                }
            }
        }
    }
}

@Composable
private fun InvoiceList(invoices: List<InvoiceEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(invoices, key = { it.id }) { invoice ->
            InvoiceCard(invoice)
        }
    }
}

@Composable
private fun InvoiceCard(invoice: InvoiceEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .background(Color(0xFF03A9F4), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, size = 14.dp, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = invoice.invoiceDate, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8BC34A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Invoice no : ${invoice.invoiceNumber}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        color = Color(0xFF8BC34A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(invoice.status, color = Color(0xFF8BC34A), fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text("Total: ${String.format(Locale.getDefault(), "%.0f", invoice.totalAmount)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Item details area placeholder (stylized like screenshot)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFFF1F3F5), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Product & Services", fontSize = 10.sp, color = Color.LightGray)
                    Text("Physiotherapy Session", fontSize = 10.sp, color = Color.LightGray)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Default.Print, contentDescription = null, size = 24.dp, tint = Color(0xFF03A9F4))
                Icon(Icons.Default.Share, contentDescription = null, size = 24.dp, tint = Color(0xFF03A9F4))
            }
        }
    }
}

// Reuse custom Icon workaround
@Composable
fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: Color) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        tint = tint
    )
}
