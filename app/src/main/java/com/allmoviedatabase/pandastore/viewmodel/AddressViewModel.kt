package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.model.address.AddressDto
import com.allmoviedatabase.pandastore.model.address.AddressRequest
import com.allmoviedatabase.pandastore.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import jakarta.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val repository: AddressRepository
) : ViewModel() {

    val addressListState = MutableLiveData<AuthState<List<AddressDto>>>()
    val operationState = MutableLiveData<AuthState<String>>() // Ekleme/Silme/Güncelleme sonucu mesaj

    private val disposable = CompositeDisposable()

    fun getAddresses() {
        addressListState.value = AuthState.Loading
        disposable.add(
            repository.getAddresses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list -> addressListState.value = AuthState.Success(list) },
                    { error -> addressListState.value = AuthState.Error(error.message ?: "Hata") }
                )
        )
    }

    fun addAddress(req: AddressRequest) {
        operationState.value = AuthState.Loading
        disposable.add(
            repository.addAddress(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { operationState.value = AuthState.Success("Adres Eklendi") },
                    { error -> operationState.value = AuthState.Error(error.message ?: "Hata") }
                )
        )
    }

    fun updateAddress(id: Int, req: AddressRequest) {
        operationState.value = AuthState.Loading
        disposable.add(
            repository.updateAddress(id, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { operationState.value = AuthState.Success("Adres Güncellendi") },
                    { error -> operationState.value = AuthState.Error(error.message ?: "Hata") }
                )
        )
    }

    fun deleteAddress(id: Int) {
        operationState.value = AuthState.Loading
        disposable.add(
            repository.deleteAddress(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        operationState.value = AuthState.Success("Adres Silindi")
                        getAddresses() // Listeyi yenile
                    },
                    { error -> operationState.value = AuthState.Error(error.message ?: "Hata") }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}