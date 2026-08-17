package com.example.sugam.ui.screens.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugam.data.local.entity.InvoiceEntity
import com.example.sugam.data.local.entity.InvoiceItemEntity
import com.example.sugam.data.repository.BillingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class InvoiceItem(
    val id: Int,
    val service: String = "",
    val quantity: String = "1",
    val unitPrice: String = "0",
    val discount: String = "0",
    val isPercentageDiscount: Boolean = true,
    val category: String = ""
) {
    val total: Double
        get() {
            val qty = quantity.toDoubleOrNull() ?: 0.0
            val price = unitPrice.toDoubleOrNull() ?: 0.0
            val disc = discount.toDoubleOrNull() ?: 0.0
            val subtotal = qty * price
            return if (isPercentageDiscount) {
                subtotal * (1 - disc / 100.0)
            } else {
                (subtotal - disc).coerceAtLeast(0.0)
            }
        }
}

class BillingViewModel(
    private val billingRepository: BillingRepository
) : ViewModel() {
    private val _invoiceDate = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
    val invoiceDate: StateFlow<String> = _invoiceDate.asStateFlow()

    private val _items = MutableStateFlow(listOf(InvoiceItem(id = 1)))
    val items: StateFlow<List<InvoiceItem>> = _items.asStateFlow()

    val totalAmount: StateFlow<Double> = _items.map { list ->
        list.sumOf { it.total }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun updateDate(date: String) {
        _invoiceDate.value = date
    }

    fun addItem() {
        val nextId = (_items.value.maxOfOrNull { it.id } ?: 0) + 1
        _items.value = _items.value + InvoiceItem(id = nextId)
    }

    fun removeItem(id: Int) {
        if (_items.value.size > 1) {
            _items.value = _items.value.filter { it.id != id }
        }
    }

    fun updateItem(updatedItem: InvoiceItem) {
        _items.value = _items.value.map {
            if (it.id == updatedItem.id) updatedItem else it
        }
    }

    fun saveInvoice(patientId: Long, onSaveSuccess: () -> Unit) {
        viewModelScope.launch {
            val invoiceNumber = (100..999).random().toString() // Simple random for now
            val invoice = InvoiceEntity(
                patientId = patientId,
                invoiceDate = _invoiceDate.value,
                invoiceNumber = invoiceNumber,
                totalAmount = totalAmount.value
            )

            val itemEntities = _items.value.map { item ->
                InvoiceItemEntity(
                    invoiceId = 0, // Will be set by repository
                    service = item.service,
                    quantity = item.quantity.toDoubleOrNull() ?: 1.0,
                    unitPrice = item.unitPrice.toDoubleOrNull() ?: 0.0,
                    discount = item.discount.toDoubleOrNull() ?: 0.0,
                    isPercentageDiscount = item.isPercentageDiscount,
                    total = item.total
                )
            }

            billingRepository.saveInvoice(invoice, itemEntities)
            
            // Reset items for next time
            _items.value = listOf(InvoiceItem(id = 1))
            onSaveSuccess()
        }
    }

    fun getInvoicesForPatient(patientId: Long) = 
        billingRepository.getInvoicesForPatient(patientId)
}
