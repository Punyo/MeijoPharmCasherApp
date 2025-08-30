package com.punyo.casherapp.ui.transactions.alltransactions

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import com.punyo.casherapp.data.product.ProductRepository
import com.punyo.casherapp.data.transaction.TransactionRepository
import com.punyo.casherapp.ui.transactions.TimePeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AllTransactionsSubScreenViewModel(
    productRepository: ProductRepository,
    transactionRepository: TransactionRepository,
) : ViewModel() {
    private val state = MutableStateFlow(AllTransactionsScreenUiState())
    val uiState: StateFlow<AllTransactionsScreenUiState> = state.asStateFlow()

    fun setTimePeriod(timePeriod: TimePeriod) {
        state.value = state.value.copy(timePeriod = timePeriod)
    }

    fun setShowDatePickerDialog(boolean: Boolean) {
        state.value = state.value.copy(showDatePickerDialog = boolean)
    }

    fun setSearchText(text: String) {
        state.value = state.value.copy(searchText = text)
    }
}

data class AllTransactionsScreenUiState
@OptIn(ExperimentalMaterial3Api::class)
constructor(
    val timePeriod: TimePeriod = TimePeriod.TODAY,
    val searchText: String = "",
    val showDatePickerDialog: Boolean = false,
)
