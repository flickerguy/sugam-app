package com.example.sugam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoice_items")
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceId: Long,
    val service: String,
    val quantity: Double,
    val unitPrice: Double,
    val discount: Double,
    val isPercentageDiscount: Boolean,
    val total: Double
)
