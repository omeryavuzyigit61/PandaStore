package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.model.product.ProductFilterParams
import com.allmoviedatabase.pandastore.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    val productsState = MutableLiveData<AuthState<List<ProductDto>>>()

    // Filtre durumunu burada tutuyoruz
    val currentFilters = ProductFilterParams()

    private val disposable = CompositeDisposable()

    init {
        loadProducts() // Uygulama açılınca ilk veriyi çek
    }

    // Ürünleri Getir (Mevcut filtrelerle)
    fun loadProducts() {
        productsState.value = AuthState.Loading

        disposable.add(
            productRepository.getProducts(currentFilters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        // Başarılı olursa listeyi bas
                        productsState.value = AuthState.Success(response.data)
                        // İstersen burada meta bilgisini de başka bir LiveData'ya atabilirsin (Sayfalama için)
                    },
                    { error ->
                        productsState.value = AuthState.Error(error.message ?: "Ürünler yüklenemedi")
                    }
                )
        )
    }

    // Arama Yap
    fun search(query: String) {
        currentFilters.page = 1 // Aramada her zaman ilk sayfaya dön
        currentFilters.searchQuery = query.ifEmpty { null }
        loadProducts()
    }

    // Filtre Uygula (Bottom Sheet'ten gelecek)
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