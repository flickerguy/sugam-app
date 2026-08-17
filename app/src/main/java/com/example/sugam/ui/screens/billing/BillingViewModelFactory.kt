package com.example.sugam.ui.screens.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sugam.data.repository.BillingRepository

class BillingViewModelFactory(
    private val billingRepository: BillingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillingViewModel::class.java)) {
            return BillingViewModel(billingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
