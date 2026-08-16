package com.example.sugam.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sugam.SugamApplication
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.data.repository.ClinicSettingsRepository
import com.example.sugam.data.repository.PhysioRepository
import com.example.sugam.ui.screens.AppointmentsScreen
import com.example.sugam.ui.screens.DashboardScreen
import com.example.sugam.ui.screens.PatientsScreen
import com.example.sugam.ui.screens.SettingsScreen
import com.example.sugam.ui.screens.patient.PatientScreen
import com.example.sugam.ui.screens.patient.PatientDetailsScreen
import com.example.sugam.ui.screens.patient.PatientViewModel
import com.example.sugam.ui.screens.patient.PatientViewModelFactory
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.sugam.ui.screens.physio.PhysioScreen
import com.example.sugam.data.repository.PatientRepository
import com.example.sugam.ui.screens.appointment.AppointmentViewModel
import com.example.sugam.ui.screens.appointment.AppointmentViewModelFactory
import com.example.sugam.ui.screens.settings.ClinicSettingsViewModel
import com.example.sugam.ui.screens.settings.ClinicSettingsViewModelFactory

private data class BottomNavItem(
    val route: String,
    val label: String
)

private val bottomNavItems = listOf(
    BottomNavItem("dashboard", "Dashboard"),
    BottomNavItem("appointments", "Appointments"),
    BottomNavItem("patients", "Patients"),
    BottomNavItem("settings", "Settings")
)

@Composable
fun SugamApp(
    appointmentRepository: AppointmentRepository,
    patientRepository: PatientRepository,
    physioRepository: PhysioRepository,
    clinicSettingsRepository: ClinicSettingsRepository
){
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route)
                        },
                        icon = {
                            Text(item.label.take(1))
                        },
                        label = {
                            Text(item.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("dashboard") {
                DashboardScreen(
                    appointmentRepository = appointmentRepository,
                    patientRepository = patientRepository,
                    physioRepository = physioRepository,
                    onNavigateToPhysios = {
                        navController.navigate("physios")
                    },
                    onCreateAppointment = {
                        navController.navigate("appointments")
                    }
                )
            }

            composable("appointments") {
                val appointmentViewModel: AppointmentViewModel = viewModel(
                    factory = AppointmentViewModelFactory(
                        appointmentRepository,
                        patientRepository,
                        physioRepository,
                        clinicSettingsRepository
                    )
                )

                AppointmentsScreen(
                    viewModel = appointmentViewModel
                )
            }

            composable("patients") {
                val application =
                    LocalContext.current.applicationContext as SugamApplication

                val patientViewModel: PatientViewModel = viewModel(
                    factory = PatientViewModelFactory(
                        patientRepository = application.patientRepository,
                        appointmentRepository = application.appointmentRepository
                    )
                )

                PatientScreen(
                    viewModel = patientViewModel,
                    onNavigateToDetails = { patientId ->
                        navController.navigate("patient_details/$patientId")
                    }
                )
            }

            composable(
                route = "patient_details/{patientId}",
                arguments = listOf(
                    navArgument("patientId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getLong("patientId") ?: 0L
                val application =
                    LocalContext.current.applicationContext as SugamApplication

                val patientViewModel: PatientViewModel = viewModel(
                    factory = PatientViewModelFactory(
                        patientRepository = application.patientRepository,
                        appointmentRepository = application.appointmentRepository
                    )
                )

                PatientDetailsScreen(
                    viewModel = patientViewModel,
                    patientId = patientId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("physios") {
                PhysioScreen(
                    physioRepository = physioRepository
                )
            }

            composable("settings") {
                val viewModel: ClinicSettingsViewModel = viewModel(
                    factory = ClinicSettingsViewModelFactory(
                        clinicSettingsRepository
                    )
                )

                SettingsScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}