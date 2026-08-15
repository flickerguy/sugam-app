package com.example.sugam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sugam.ui.SugamApp
import com.example.sugam.ui.theme.SugamTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val application = application as SugamApplication

        setContent {
            SugamApp(
                appointmentRepository = application.appointmentRepository,
                patientRepository = application.patientRepository,
                physioRepository = application.physioRepository,
                clinicSettingsRepository =
                    (application as SugamApplication).clinicSettingsRepository
            )
        }
    }
}