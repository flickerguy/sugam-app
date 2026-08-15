package com.example.sugam.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sugam.data.local.dao.AppointmentDao
import com.example.sugam.data.local.entity.AppointmentEntity
import com.example.sugam.data.local.entity.PatientEntity
import com.example.sugam.data.local.dao.PatientDao
import com.example.sugam.data.local.dao.PhysioDao
import com.example.sugam.data.local.entity.ClinicSettingsEntity
import com.example.sugam.data.local.entity.PhysioEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sugam.data.local.dao.ClinicSettingsDao

@Database(
    entities = [
        AppointmentEntity::class,
        PatientEntity::class,
        PhysioEntity::class,
        ClinicSettingsEntity::class
    ],
    version = 4,
    exportSchema = false
)


abstract class AppDatabase : RoomDatabase() {

    abstract fun appointmentDao(): AppointmentDao
    abstract fun patientDao(): PatientDao
    abstract fun physioDao(): PhysioDao

    abstract fun clinicSettingsDao(): ClinicSettingsDao
}


val MIGRATION_3_4 = object : Migration(3, 4) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS clinic_settings (
                id INTEGER NOT NULL,
                clinicName TEXT NOT NULL,
                workingStartTime TEXT NOT NULL,
                workingEndTime TEXT NOT NULL,
                appointmentDurationMinutes INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            INSERT INTO clinic_settings (
                id,
                clinicName,
                workingStartTime,
                workingEndTime,
                appointmentDurationMinutes
            )
            VALUES (
                1,
                'Sugam Physiotherapy Clinic',
                '09:00',
                '19:00',
                30
            )
            """.trimIndent()
        )
    }
}

