package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.repository.AddressRepository
import com.allmoviedatabase.pandastore.repository.CartRepository
import com.allmoviedatabase.pandastore.model.address.AddressDto
import com.allmoviedatabase.pandastore.model.order.CreateOrderRequest
import com.allmoviedatabase.pandastore.model.order.OrderResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,    // Sipariş vermek için
    private val addressRepository: AddressRepository // Adresleri çekmek için (Zaten varmış)
) : ViewModel() {

    // Sipariş Durumu
    private val _orderState = MutableLiveData<OrderState>()
    val orderState: LiveData<OrderState> get() = _orderState

    // Kayıtlı Adres Listesi
    private val _savedAddresses = MutableLiveData<List<AddressDto>>()
    val savedAddresses: LiveData<List<AddressDto>> get() = _savedAddresses

    private val disposable = CompositeDisposable()

    init {
        loadSavedAddresses()
    }

    // 1. MEVCUT ADRESLERİ ÇEK (Senin yazdığın repository'yi kullanıyoruz)
    private fun loadSavedAddresses() {
        disposable.add(
            addressRepository.getAddresses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { addressList ->
                        _savedAddresses.value = addressList
                    },
                    { error ->
                        // Hata olursa (veya hiç adres yoksa) boş liste döner, sorun yok
                        _savedAddresses.value = emptyList()
                    }
                )
        )
    }

    // 2. SİPARİŞİ TAMAMLA
    fun submitOrder(
        name: String, phone: String, city: String, district: String,
        address: String, zipCode: String, note: String
    ) {
        // Validasyon
        if (name.isEmpty() || phone.isEmpty() || city.isEmpty() || district.isEmpty() || address.isEmpty()) {
            _orderState.value = OrderState.ValidationError("Lütfen zorunlu alanları (*) doldurun.")
            return
        }

        // Request Oluştur
        val request = CreateOrderRequest(
            shippingName = name,
            shippingPhone = phone,
            shippingCity = city,
            shippingDistrict = district,
            shippingAddress = address,
            shippingZipCode = zipCode,
            customerNote = note
        )

        _orderState.value = OrderState.Loading

        // CartRepository üzerinden sipariş oluştur
        disposable.add(
            cartRepository.createOrder(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        _orderState.value = OrderState.Success(response)
                    },
                    { error ->
                        _orderState.value = OrderState.Error(error.message ?: "Sipariş oluşturulurken hata çıktı.")
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

// UI Durumlarını Yönetmek İçin Sealed Class
sealed class OrderState {
    object Loading : OrderState()
    data class Success(val orderResponse: OrderResponse) : OrderState()
    data class Error(val message: String) : OrderState()
    data class ValidationError(val message: String) : OrderState()
}