package com.example.sugam

import android.app.Application
import androidx.room.Room
import com.example.sugam.data.local.AppDatabase
import com.example.sugam.data.local.MIGRATION_3_4
import com.example.sugam.data.repository.AppointmentRepository
import com.example.sugam.data.repository.ClinicSettingsRepository
import com.example.sugam.data.repository.PatientRepository
import com.example.sugam.data.repository.PhysioRepository

class SugamApplication : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "sugam.db"
        )
            .fallbackToDestructiveMigration(true)
            .addMigrations(MIGRATION_3_4)
            .build()
    }

    val appointmentRepository: AppointmentRepository by lazy {
        AppointmentRepository(
            database.appointmentDao()
        )
    }

    val patientRepository: PatientRepository by lazy {
        PatientRepository(
            database.patientDao()
        )
    }

    val physioRepository: PhysioRepository by lazy {
        PhysioRepository(
            database.physioDao()
        )
    }

    val clinicSettingsRepository by lazy {
        ClinicSettingsRepository(database.clinicSettingsDao())
    }
}