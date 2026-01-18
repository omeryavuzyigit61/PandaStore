package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.repository.OrderRepository
import com.allmoviedatabase.pandastore.model.order.OrderDto
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    // Ham veri (Tüm siparişler)
    private var allOrders: List<OrderDto> = emptyList()

    // UI Durumu (Dikkat: İsmini değiştirdik)
    private val _ordersState = MutableLiveData<OrderListState>()
    val ordersState: LiveData<OrderListState> get() = _ordersState

    private val disposable = CompositeDisposable()

    init {
        getOrders()
    }

    fun getOrders() {
        _ordersState.value = OrderListState.Loading
        disposable.add(
            repository.getOrders()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list ->
                        allOrders = list
                        filterOrders(TimeFilter.ALL)
                    },
                    { error ->
                        _ordersState.value = OrderListState.Error(error.message ?: "Siparişler yüklenemedi")
                    }
                )
        )
    }

    fun filterOrders(filter: TimeFilter) {
        if (allOrders.isEmpty()) {
            _ordersState.value = OrderListState.Empty
            return
        }

        val filteredList = if (filter == TimeFilter.ALL) {
            allOrders
        } else {
            val calendar = Calendar.getInstance()
            when (filter) {
                TimeFilter.LAST_WEEK -> calendar.add(Calendar.DAY_OF_YEAR, -7)
                TimeFilter.LAST_MONTH -> calendar.add(Calendar.MONTH, -1)
                TimeFilter.LAST_3_MONTHS -> calendar.add(Calendar.MONTH, -3)
                TimeFilter.LAST_YEAR -> calendar.add(Calendar.YEAR, -1)
                else -> {}
            }
            val thresholdDate = calendar.time
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

            allOrders.filter {
                try {
                    val orderDate = parser.parse(it.createdAt)
                    orderDate?.after(thresholdDate) == true
                } catch (e: Exception) {
                    true
                }
            }
        }

        if (filteredList.isEmpty()) {
            _ordersState.value = OrderListState.Empty
        } else {
            // Success artık List<OrderDto> kabul ediyor
            _ordersState.value = OrderListState.Success(filteredList.sortedByDescending { it.createdAt })
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

// --- YENİ SEALED CLASS (İSİM ÇAKIŞMASINI ÖNLEMEK İÇİN) ---
sealed class OrderListState {
    object Loading : OrderListState()
    object Empty : OrderListState()
    // Buradaki Success LİSTE tutuyor
    data class Success(val data: List<OrderDto>) : OrderListState()
    data class Error(val message: String) : OrderListState()
}

enum class TimeFilter {
    ALL, LAST_WEEK, LAST_MONTH, LAST_3_MONTHS, LAST_YEAR
}