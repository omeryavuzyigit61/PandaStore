package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.repository.CartRepository
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.model.product.ProductFilterParams
import com.allmoviedatabase.pandastore.repository.ProductRepository
import com.allmoviedatabase.pandastore.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository, // <--- YENİ EKLENDİ
    private val tokenManager: TokenManager      // <--- YENİ EKLENDİ (Giriş kontrolü için)
) : ViewModel() {

    val productsState = MutableLiveData<AuthState<List<ProductDto>>>()

    // Sepete Ekleme Sonucu Mesajı
    private val _addToCartMessage = MutableLiveData<String?>()
    val addToCartMessage: LiveData<String?> get() = _addToCartMessage

    // Filtre durumunu burada tutuyoruz
    val currentFilters = ProductFilterParams()

    private val disposable = CompositeDisposable()

    init {
        loadProducts()
    }

    // Ürünleri Getir
    fun loadProducts() {
        productsState.value = AuthState.Loading

        disposable.add(
            productRepository.getProducts(currentFilters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        productsState.value = AuthState.Success(response.data)
                    },
                    { error ->
                        productsState.value = AuthState.Error(error.message ?: "Ürünler yüklenemedi")
                    }
                )
        )
    }

    // --- YENİ EKLENEN: SEPETE EKLE FONKSİYONU ---
    fun addToCart(productId: Int) {
        // Basit giriş kontrolü
        if (tokenManager.getAccessToken().isNullOrEmpty()) {
            _addToCartMessage.value = "Sepete eklemek için giriş yapmalısınız."
            return
        }

        disposable.add(
            cartRepository.addToCart(productId, 1) // Varsayılan 1 adet
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _addToCartMessage.value = "Ürün sepete eklendi!"
                    },
                    { error ->
                        _addToCartMessage.value = "Hata: ${error.localizedMessage}"
                    }
                )
        )
    }

    // Mesajı temizlemek için (Toast tekrar etmesin diye)
    fun clearCartMessage() {
        _addToCartMessage.value = null
    }

    // Arama Yap
    fun search(query: String) {
        currentFilters.page = 1
        currentFilters.searchQuery = query.ifEmpty { null }
        loadProducts()
    }

    // Filtre Uygula
    fun applyFilters(min: Double?, max: Double?, sort: String?) {
        currentFilters.page = 1
        currentFilters.minPrice = min
        currentFilters.maxPrice = max
        currentFilters.sortBy = sort
        loadProducts()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}