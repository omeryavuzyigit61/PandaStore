package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.repository.ProductRepository
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.model.review.CreateReviewRequest
import com.allmoviedatabase.pandastore.model.review.ReviewDto
import com.allmoviedatabase.pandastore.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val disposables = CompositeDisposable()

    // Data Holders
    private val _productDetail = MutableLiveData<ProductDto>()
    val productDetail: LiveData<ProductDto> get() = _productDetail

    private val _reviews = MutableLiveData<List<ReviewDto>>()
    val reviews: LiveData<List<ReviewDto>> get() = _reviews

    // Benim Yorumum (Varsa)
    private val _myReview = MutableLiveData<ReviewDto?>()
    val myReview: LiveData<ReviewDto?> get() = _myReview

    // İşlem Durumları
    private val _reviewActionSuccess = MutableLiveData<String?>() // Başarı mesajı
    val reviewActionSuccess: LiveData<String?> get() = _reviewActionSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

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

    fun getReviews(productId: Int) {
        disposables.add(
            repository.getProductReviews(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _reviews.value = it },
                    { _reviews.value = emptyList() } // Hata olsa da boş liste dön
                )
        )
    }

    // LİSTE İÇİNDE BENİM YORUMUM VAR MI?
    fun checkMyReview(reviewList: List<ReviewDto>) {
        val currentUserId = tokenManager.getUserId() // String döner

        if (currentUserId != null) {
            val foundReview = reviewList.find { it.userId == currentUserId }
            _myReview.value = foundReview
        } else {
            _myReview.value = null
        }
    }

    // EKLEME
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
                        getReviews(productId) // Listeyi yenile
                    },
                    { _isLoading.value = false; _error.value = it.localizedMessage }
                )
        )
    }

    // GÜNCELLEME
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
                        getReviews(productId) // Listeyi yenile
                    },
                    { _isLoading.value = false; _error.value = it.localizedMessage }
                )
        )
    }

    // SİLME
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
                        _myReview.value = null // Artık benim yorumum yok
                        getReviews(productId) // Listeyi yenile
                    },
                    { _isLoading.value = false; _error.value = it.localizedMessage }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}