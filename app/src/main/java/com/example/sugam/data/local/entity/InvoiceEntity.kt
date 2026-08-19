package com.example.sugam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val invoiceDate: String,
    val invoiceNumber: String,
    val totalAmount: Double,
    val status: String = "FULLY PAID"
)
