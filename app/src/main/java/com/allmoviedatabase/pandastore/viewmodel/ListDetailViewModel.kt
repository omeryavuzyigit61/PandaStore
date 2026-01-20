package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.model.lists.CustomListDto
import com.allmoviedatabase.pandastore.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val repository: ListRepository
) : ViewModel() {

    private val _listDetail = MutableLiveData<CustomListDto>()
    val listDetail: LiveData<CustomListDto> get() = _listDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val disposable = CompositeDisposable()

    fun getListDetail(id: Int) {
        _isLoading.value = true
        disposable.add(
            repository.getListDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list ->
                        _isLoading.value = false
                        _listDetail.value = list
                    },
                    { e ->
                        _isLoading.value = false
                        _error.value = e.localizedMessage ?: "Liste detayları yüklenemedi."
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}