package com.example.sugam.data.local.dao

import androidx.room.*
import com.example.sugam.data.local.entity.InvoiceEntity
import com.example.sugam.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Insert
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Insert
    suspend fun insertInvoiceItems(items: List<InvoiceItemEntity>)

    @Transaction
    @Query("SELECT * FROM invoices WHERE patientId = :patientId ORDER BY id DESC")
    fun getInvoicesForPatient(patientId: Long): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun getItemsForInvoice(invoiceId: Long): List<InvoiceItemEntity>

    @Transaction
    suspend fun saveInvoiceWithItems(invoice: InvoiceEntity, items: List<InvoiceItemEntity>) {
        val invoiceId = insertInvoice(invoice)
        val itemsWithId = items.map { it.copy(invoiceId = invoiceId) }
        insertInvoiceItems(itemsWithId)
    }
}
