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

import com.example.sugam.data.local.entity.InvoiceEntity
import com.example.sugam.data.local.entity.InvoiceItemEntity
import com.example.sugam.data.local.dao.InvoiceDao

@Database(
    entities = [
        AppointmentEntity::class,
        PatientEntity::class,
        PhysioEntity::class,
        ClinicSettingsEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class
    ],
    version = 5,
    exportSchema = false
)


abstract class AppDatabase : RoomDatabase() {

    abstract fun appointmentDao(): AppointmentDao
    abstract fun patientDao(): PatientDao
    abstract fun physioDao(): PhysioDao

    abstract fun clinicSettingsDao(): ClinicSettingsDao

    abstract fun invoiceDao(): InvoiceDao
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

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS invoices (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                patientId INTEGER NOT NULL,
                invoiceDate TEXT NOT NULL,
                invoiceNumber TEXT NOT NULL,
                totalAmount REAL NOT NULL,
                status TEXT NOT NULL
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS invoice_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                invoiceId INTEGER NOT NULL,
                service TEXT NOT NULL,
                quantity REAL NOT NULL,
                unitPrice REAL NOT NULL,
                discount REAL NOT NULL,
                isPercentageDiscount INTEGER NOT NULL,
                total REAL NOT NULL
            )
            """.trimIndent()
        )
    }
}

val PREPOPULATE_CALLBACK = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.execSQL(
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

