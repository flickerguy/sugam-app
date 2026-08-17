package com.example.sugam.data.repository

import com.example.sugam.data.local.dao.InvoiceDao
import com.example.sugam.data.local.entity.InvoiceEntity
import com.example.sugam.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

class BillingRepository(
    private val invoiceDao: InvoiceDao
) {
    suspend fun saveInvoice(invoice: InvoiceEntity, items: List<InvoiceItemEntity>) {
        invoiceDao.saveInvoiceWithItems(invoice, items)
    }

    fun getInvoicesForPatient(patientId: Long): Flow<List<InvoiceEntity>> {
        return invoiceDao.getInvoicesForPatient(patientId)
    }

    suspend fun getItemsForInvoice(invoiceId: Long): List<InvoiceItemEntity> {
        return invoiceDao.getItemsForInvoice(invoiceId)
    }
}
