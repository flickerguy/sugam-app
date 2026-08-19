package com.example.sugam.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sugam.SugamApplication
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.data.repository.ClinicSettingsRepository
import com.example.sugam.data.repository.PatientRepository
import com.example.sugam.data.repository.PhysioRepository
import com.example.sugam.ui.screens.patient.AddPatientScreen
import com.example.sugam.ui.screens.patient.PatientDetailsScreen
import com.example.sugam.ui.screens.patient.PatientScreen
import com.example.sugam.ui.screens.patient.PatientViewModel
import com.example.sugam.ui.screens.patient.PatientViewModelFactory
import com.example.sugam.ui.screens.appointment.AddAppointmentScreen
import com.example.sugam.ui.screens.appointment.AppointmentViewModel
import com.example.sugam.ui.screens.appointment.AppointmentViewModelFactory
import com.example.sugam.ui.screens.billing.AddInvoiceScreen
import com.example.sugam.ui.screens.billing.BillingDashboardScreen
import com.example.sugam.ui.screens.billing.BillingHistoryScreen
import com.example.sugam.ui.screens.billing.BillingViewModel
import com.example.sugam.ui.screens.billing.BillingViewModelFactory
import com.example.sugam.ui.screens.home.HomeScreen
import com.example.sugam.ui.screens.home.HomeViewModel
import com.example.sugam.ui.screens.home.HomeViewModelFactory
import com.example.sugam.ui.screens.physio.PhysioScreen
import com.example.sugam.ui.screens.settings.ClinicSettingsViewModel
import com.example.sugam.ui.screens.settings.ClinicSettingsViewModelFactory
import com.example.sugam.ui.screens.SettingsScreen

@Composable
fun SugamApp(
    appointmentRepository: AppointmentRepository,
    patientRepository: PatientRepository,
    physioRepository: PhysioRepository,
    clinicSettingsRepository: ClinicSettingsRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val application = LocalContext.current.applicationContext as SugamApplication

    val appointmentViewModel: AppointmentViewModel = viewModel(
        factory = AppointmentViewModelFactory(
            appointmentRepository,
            patientRepository,
            physioRepository,
            clinicSettingsRepository
        )
    )

    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModelFactory(
            patientRepository,
            appointmentRepository
        )
    )

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            appointmentRepository,
            patientRepository,
            physioRepository
        )
    )

    val billingViewModel: BillingViewModel = viewModel(
        factory = BillingViewModelFactory(application.billingRepository)
    )

    var showQuickActionDialog by remember { mutableStateOf(false) }

    if (showQuickActionDialog) {
        QuickActionDialog(
            onDismiss = { showQuickActionDialog = false },
            onNewAppointment = {
                showQuickActionDialog = false
                appointmentViewModel.resetForm()
                navController.navigate("add_appointment")
            },
            onNewPatient = {
                showQuickActionDialog = false
                navController.navigate("add_patient")
            },
            onNewInvoice = {
                showQuickActionDialog = false
                navController.navigate("billing")
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(80.dp),
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavigationItem(
                            label = "Home",
                            icon = Icons.Default.Home,
                            selected = currentRoute == "home",
                            onClick = { navController.navigate("home") }
                        )
                        NavigationItem(
                            label = "Patients",
                            icon = Icons.Default.AssignmentInd,
                            selected = currentRoute == "patients",
                            onClick = { navController.navigate("patients") }
                        )
                        
                        Spacer(modifier = Modifier.width(64.dp)) // Gap for FAB
                        
                        NavigationItem(
                            label = "Billing",
                            icon = Icons.Default.ReceiptLong,
                            selected = currentRoute == "billing",
                            onClick = { navController.navigate("billing") }
                        )
                        NavigationItem(
                            label = "Profile",
                            icon = Icons.Default.AccountCircle,
                            selected = currentRoute == "settings",
                            onClick = { navController.navigate("settings") }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showQuickActionDialog = true
                },
                containerColor = Color(0xFF03A9F4),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .offset(y = 56.dp) // Lift slightly from the absolute bottom but inside the bar
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Quick Actions",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = homeViewModel,
                    onEditAppointment = { id ->
                        val appointment = homeViewModel.appointments.value.find { it.id == id }
                        if (appointment != null) {
                            appointmentViewModel.startEditingAppointment(
                                com.example.sugam.data.local.entity.AppointmentEntity(
                                    id = appointment.id,
                                    patientId = appointment.patientId,
                                    physioId = appointment.physioId,
                                    appointmentDate = appointment.appointmentDate,
                                    startTime = appointment.startTime,
                                    endTime = appointment.endTime,
                                    status = appointment.status,
                                    notes = appointment.notes
                                )
                            )
                            navController.navigate("add_appointment")
                        }
                    }
                )
            }

            composable("add_appointment") {
                AddAppointmentScreen(
                    viewModel = appointmentViewModel,
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            composable("patients") {
                PatientScreen(
                    viewModel = patientViewModel,
                    onNavigateToDetails = { patientId ->
                        navController.navigate("patient_details/$patientId")
                    },
                    onAddPatient = {
                        navController.navigate("add_patient")
                    }
                )
            }

            composable("add_patient") {
                AddPatientScreen(
                    viewModel = patientViewModel,
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = "patient_details/{patientId}",
                arguments = listOf(
                    navArgument("patientId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getLong("patientId") ?: 0L
                PatientDetailsScreen(
                    viewModel = patientViewModel,
                    patientId = patientId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("physios") {
                PhysioScreen(physioRepository = physioRepository)
            }

            composable("billing") {
                BillingDashboardScreen(
                    viewModel = patientViewModel,
                    onPatientSelected = { patientId ->
                        navController.navigate("billing_history/$patientId")
                    }
                )
            }

            composable(
                route = "billing_history/{patientId}",
                arguments = listOf(navArgument("patientId") { type = NavType.LongType })
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getLong("patientId") ?: 0L
                BillingHistoryScreen(
                    patientViewModel = patientViewModel,
                    billingViewModel = billingViewModel,
                    patientId = patientId,
                    onBack = { navController.popBackStack() },
                    onAddInvoice = {
                        navController.navigate("add_invoice/$patientId")
                    }
                )
            }

            composable(
                route = "add_invoice/{patientId}",
                arguments = listOf(navArgument("patientId") { type = NavType.LongType })
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getLong("patientId") ?: 0L
                // We could pass patientId to BillingViewModel if needed
                AddInvoiceScreen(
                    viewModel = billingViewModel,
                    patientId = patientId,
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            composable("settings") {
                val viewModel: ClinicSettingsViewModel = viewModel(
                    factory = ClinicSettingsViewModelFactory(clinicSettingsRepository)
                )
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionDialog(
    onDismiss: () -> Unit,
    onNewAppointment: () -> Unit,
    onNewPatient: () -> Unit,
    onNewInvoice: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        content = {
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp)
                                .background(Color(0xFFD32F2F), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onNewAppointment,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4)),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(24.dp))
                            Text("New Appointment", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onNewPatient,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(24.dp))
                            Text("New Patient", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onNewInvoice,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(24.dp))
                            Text("New Invoice", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    )
}

@Composable
private fun NavigationItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF03A9F4) else Color.Gray
    
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
