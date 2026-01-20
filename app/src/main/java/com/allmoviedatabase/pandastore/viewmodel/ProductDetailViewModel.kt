package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.model.lists.CustomListDto
import com.allmoviedatabase.pandastore.repository.ProductRepository
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.model.review.CreateReviewRequest
import com.allmoviedatabase.pandastore.model.review.ReviewDto
import com.allmoviedatabase.pandastore.repository.CartRepository
import com.allmoviedatabase.pandastore.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val disposables = CompositeDisposable()

    // --- DATA HOLDERS ---
    private val _productDetail = MutableLiveData<ProductDto>()
    val productDetail: LiveData<ProductDto> get() = _productDetail

    private val _reviews = MutableLiveData<List<ReviewDto>>()
    val reviews: LiveData<List<ReviewDto>> get() = _reviews

    private val _myReview = MutableLiveData<ReviewDto?>()
    val myReview: LiveData<ReviewDto?> get() = _myReview

    // YENİ EKLENENLER: FAVORİ VE LİSTELER
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private val _userLists = MutableLiveData<List<CustomListDto>>()
    val userLists: LiveData<List<CustomListDto>> get() = _userLists

    // --- İŞLEM DURUMLARI ---
    private val _reviewActionSuccess = MutableLiveData<String?>() // Yorum ve Liste ekleme mesajları
    val reviewActionSuccess: LiveData<String?> get() = _reviewActionSuccess

    private val _addToCartSuccess = MutableLiveData<String?>()
    val addToCartSuccess: LiveData<String?> get() = _addToCartSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // --- 1. ÜRÜN DETAYI ---
    fun getProductDetail(id: Int) {
        _isLoading.value = true
        disposables.add(
            repository.getProductDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _productDetail.value = it; _isLoading.value = false },
                    { _error.value = it.localizedMessage; _isLoading.value = false }
                )
        )
    }

    // --- 2. YORUMLARI GETİR ---
    fun getReviews(productId: Int) {
        disposables.add(
            repository.getProductReviews(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _reviews.value = it },
                    { _reviews.value = emptyList() }
                )
        )
    }

    fun checkMyReview(reviewList: List<ReviewDto>) {
        val currentUserId = tokenManager.getUserId()
        if (currentUserId != null) {
            val foundReview = reviewList.find { it.userId == currentUserId }
            _myReview.value = foundReview
        } else {
            _myReview.value = null
        }
    }

    // --- 3. FAVORİ İŞLEMLERİ (YENİ) ---
    fun checkFavoriteStatus(productId: Int) {
        if (tokenManager.getAccessToken() == null) return

        disposables.add(
            repository.checkFavoriteStatus(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _isFavorite.value = it },
                    { it.printStackTrace() }
                )
        )
    }

    fun toggleFavorite(productId: Int) {
        if (tokenManager.getAccessToken() == null) {
            _error.value = "Favoriye eklemek için giriş yapmalısınız."
            return
        }

        val currentStatus = _isFavorite.value ?: false
        val request = if (currentStatus) repository.removeFromFavorites(productId) else repository.addToFavorites(productId)

        disposables.add(
            request
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _isFavorite.value = !currentStatus
                        val msg = if (!currentStatus) "Favorilere eklendi ❤️" else "Favorilerden çıkarıldı 💔"
                        _reviewActionSuccess.value = msg
                    },
                    { _error.value = "İşlem başarısız: ${it.localizedMessage}" }
                )
        )
    }

    // --- 4. LİSTE İŞLEMLERİ (YENİ) ---
    fun fetchUserLists() {
        if (tokenManager.getAccessToken() == null) {
            _error.value = "Listelerinizi görmek için giriş yapmalısınız."
            return
        }

        disposables.add(
            repository.getUserLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _userLists.value = it },
                    { _error.value = "Listeler alınamadı: ${it.localizedMessage}" }
                )
        )
    }

    fun addProductToCustomList(listId: Int, productId: Int) {
        _isLoading.value = true
        disposables.add(
            repository.addProductToList(listId, productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _isLoading.value = false
                        _reviewActionSuccess.value = "Ürün listeye kaydedildi ✅"
                    },
                    {
                        _isLoading.value = false
                        _error.value = "Listeye eklenemedi: ${it.localizedMessage}"
                    }
                )
        )
    }

    // --- 5. YORUM CRUD ---
    fun submitReview(productId: Int, rating: Int, title: String, comment: String) {
        _isLoading.value = true
        val request = CreateReviewRequest(productId, rating, title, comment)
        disposables.add(
            repository.addReview(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _isLoading.value = false
                        _reviewActionSuccess.value = "Yorumunuz eklendi!"
                        getReviews(productId)
                    },
                    { _isLoading.value = false; _error.value = it.localizedMessage }
                )
        )
    }

    fun updateReview(reviewId: Int, productId: Int, rating: Int, title: String, comment: String) {
        _isLoading.value = true
        val request = CreateReviewRequest(productId, rating, title, comment)
        disposables.add(
            repository.updateReview(reviewId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _isLoading.value = false
                        _reviewActionSuccess.value = "Yorumunuz güncellendi!"
                        getReviews(productId)
                    },
                    { _isLoading.value = false; _error.value = it.localizedMessage }
                )
        )
    }

    fun deleteReview(reviewId: Int, productId: Int) {
        _isLoading.value = true
        disposables.add(
            repository.deleteReview(reviewId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _isLoading.value = false
                        _reviewActionSuccess.value = "Yorumunuz silindi."
                        _myReview.value = null
                        getReviews(productId)
                    },
                    { _isLoading.value = false; _error.value = it.localizedMessage }
                )
        )
    }

    // --- 6. SEPET ---
    fun addToCart(productId: Int, quantity: Int = 1) {
        if (tokenManager.getAccessToken().isNullOrEmpty()) {
            _error.value = "Sepete eklemek için giriş yapmalısınız."
            return
        }
        _isLoading.value = true
        disposables.add(
            cartRepository.addToCart(productId, quantity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _isLoading.value = false
                        _addToCartSuccess.value = "Ürün sepete eklendi!"
                    },
                    {
                        _isLoading.value = false
                        _error.value = "Sepete eklenemedi: ${it.message}"
                    }
                )
        )
    }

    fun clearAddToCartMessage() {
        _addToCartSuccess.value = null
    }

    fun clearMessages() {
        _reviewActionSuccess.value = null
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}