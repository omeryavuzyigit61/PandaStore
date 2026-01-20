package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.model.lists.CreateListRequest
import com.allmoviedatabase.pandastore.model.lists.CustomListDto
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.repository.CartRepository
import com.allmoviedatabase.pandastore.repository.ListRepository
import com.allmoviedatabase.pandastore.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MyListsViewModel @Inject constructor(
    private val listRepository: ListRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<MultiState>()
    val uiState: LiveData<MultiState> get() = _uiState

    private val _actionMessage = MutableLiveData<String?>()
    val actionMessage: LiveData<String?> get() = _actionMessage

    private val disposable = CompositeDisposable()

    // Başlangıçta Beğendiklerim gelsin istersen burayı değiştir
    init {
        getFavorites()
    }

    // TAB 0: BEĞENDİKLERİM (Favori Ürünler)
    fun getFavorites() {
        _uiState.value = MultiState.Loading
        disposable.add(
            productRepository.getFavorites() // Artık bu fonksiyon var!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { products ->
                        if (products.isEmpty()) {
                            _uiState.value = MultiState.Empty
                        } else {
                            _uiState.value = MultiState.SuccessProducts(products)
                        }
                    },
                    {
                        _uiState.value = MultiState.Error(it.localizedMessage ?: "Favoriler yüklenemedi")
                    }
                )
        )
    }

    // TAB 1: LİSTELERİM
    fun getMyLists() {
        _uiState.value = MultiState.Loading
        disposable.add(
            listRepository.getMyLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { lists ->
                        if (lists.isEmpty()) _uiState.value = MultiState.Empty
                        else _uiState.value = MultiState.SuccessLists(lists)
                    },
                    { _uiState.value = MultiState.Error(it.message ?: "Hata") }
                )
        )
    }

    // TAB 2: KEŞFET
    fun getDiscoverLists() {
        _uiState.value = MultiState.Loading
        disposable.add(
            listRepository.getPublicLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { lists ->
                        if (lists.isEmpty()) _uiState.value = MultiState.Empty
                        else _uiState.value = MultiState.SuccessLists(lists)
                    },
                    { _uiState.value = MultiState.Error(it.message ?: "Hata") }
                )
        )
    }

    // --- LİSTE İŞLEMLERİ (CRUD) ---
    fun createList(name: String, desc: String?, icon: String, color: String) {
        val request = CreateListRequest(name, desc, isPrivate = true, coverColor = color, icon = icon)
        _uiState.value = MultiState.Loading
        disposable.add(
            listRepository.createList(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { getMyLists() }, // Oluşturunca Listelerim sekmesine dön
                    { _uiState.value = MultiState.Error("Oluşturulamadı: ${it.message}") }
                )
        )
    }

    fun deleteList(id: Int) {
        disposable.add(
            listRepository.deleteList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { getMyLists() },
                    { _uiState.value = MultiState.Error("Silinemedi") }
                )
        )
    }

    fun updateList(id: Int, name: String, isPrivate: Boolean) {
        _uiState.value = MultiState.Loading
        disposable.add(
            listRepository.updateList(id, name, isPrivate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { getMyLists() },
                    { _uiState.value = MultiState.Error("Hata: ${it.message}") }
                )
        )
    }

    fun addToCart(productId: Int) {
        disposable.add(
            cartRepository.addToCart(productId, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _actionMessage.value = "Ürün sepete eklendi ✅"
                    },
                    { error ->
                        _actionMessage.value = "Sepete eklenirken hata: ${error.message}"
                    }
                )
        )
    }

    fun removeFromFavorites(productId: Int) {
        disposable.add(
            productRepository.removeFromFavorites(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _actionMessage.value = "Favorilerden çıkarıldı"
                        // Listeyi anında yenile ki ürün ekrandan gitsin
                        getFavorites()
                    },
                    { error ->
                        _actionMessage.value = "Hata: ${error.message}"
                    }
                )
        )
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

// ARTIK 2 FARKLI SUCCESS DURUMUMUZ VAR
sealed class MultiState {
    object Loading : MultiState()
    object Empty : MultiState()
    data class SuccessLists(val data: List<CustomListDto>) : MultiState()
    data class SuccessProducts(val data: List<ProductDto>) : MultiState()
    data class Error(val message: String) : MultiState()
}