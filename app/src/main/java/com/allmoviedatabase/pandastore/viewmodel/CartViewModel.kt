package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.repository.CartRepository
import com.allmoviedatabase.pandastore.model.cart.CartResponse
import com.allmoviedatabase.pandastore.model.order.CreateOrderRequest
import com.allmoviedatabase.pandastore.model.order.OrderResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository
) : ViewModel() {

    // Sepet verisi
    private val _cartState = MutableLiveData<CartResponse?>()
    val cartState: LiveData<CartResponse?> get() = _cartState

    // Yükleniyor mu?
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Hata Mesajı
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Sipariş Başarılı Oldu mu? (Checkout için)
    private val _orderSuccess = MutableLiveData<OrderResponse?>()
    val orderSuccess: LiveData<OrderResponse?> get() = _orderSuccess

    private val disposable = CompositeDisposable()

    // Sayfa açılınca çağrılacak
    fun loadCart() {
        _isLoading.value = true
        disposable.add(
            repository.getCart()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        _cartState.value = response
                        _isLoading.value = false
                    },
                    { e ->
                        // 404 veya boş sepet hatası gelirse UI'ı boş göster
                        _cartState.value = null
                        _error.value = e.localizedMessage
                        _isLoading.value = false
                    }
                )
        )
    }

    // Miktar Güncelleme (+ / - butonları için)
    fun updateQuantity(itemId: Int, newQuantity: Int) {
        if (newQuantity < 1) return

        _isLoading.value = true
        disposable.add(
            repository.updateQuantity(itemId, newQuantity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        // Başarılı olunca sepeti tekrar çek ki fiyatlar güncellensin
                        loadCart()
                    },
                    { e ->
                        _isLoading.value = false
                        _error.value = "Güncellenemedi: ${e.message}"
                    }
                )
        )
    }

    // Ürün Silme
    fun deleteItem(itemId: Int) {
        _isLoading.value = true
        disposable.add(
            repository.deleteItem(itemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { loadCart() },
                    { e ->
                        _isLoading.value = false
                        _error.value = "Silinemedi: ${e.message}"
                    }
                )
        )
    }

    // Şimdilik sadece metod tanımı, Checkout ekranında kullanacağız
    fun createOrder(request: CreateOrderRequest) {
        // Burayı CheckoutFragment'a saklayacağız ama burada dursun
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}