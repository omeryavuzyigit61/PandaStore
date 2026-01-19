package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.model.lists.CreateListRequest
import com.allmoviedatabase.pandastore.repository.ListRepository
import com.allmoviedatabase.pandastore.model.lists.CustomListDto
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MyListsViewModel @Inject constructor(
    private val repository: ListRepository
) : ViewModel() {

    private val _listsState = MutableLiveData<MyListsState>()
    val listsState: LiveData<MyListsState> get() = _listsState

    private val disposable = CompositeDisposable()

    init {
        getMyLists()
    }

    fun getMyLists() {
        _listsState.value = MyListsState.Loading
        disposable.add(
            repository.getMyLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { lists ->
                        if (lists.isEmpty()) {
                            _listsState.value = MyListsState.Empty
                        } else {
                            _listsState.value = MyListsState.Success(lists)
                        }
                    },
                    { error ->
                        _listsState.value =
                            MyListsState.Error(error.message ?: "Listeler yüklenemedi")
                    }
                )
        )
    }

    fun createList(name: String, desc: String?, icon: String, color: String) {
        val request =
            CreateListRequest(name, desc, isPrivate = true, coverColor = color, icon = icon)

        _listsState.value = MyListsState.Loading
        disposable.add(
            repository.createList(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        // Başarılı olunca listeyi yenile
                        getMyLists()
                    },
                    { error ->
                        _listsState.value = MyListsState.Error("Oluşturulamadı: ${error.message}")
                    }
                )
        )
    }

    // Listeyi silmek için
    fun deleteList(id: Int) {
        disposable.add(
            repository.deleteList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { getMyLists() },
                    { error -> _listsState.value = MyListsState.Error("Silinemedi") }
                )
        )
    }

    fun updateList(id: Int, name: String, isPrivate: Boolean) {
        _listsState.value = MyListsState.Loading
        disposable.add(
            repository.updateList(id, name, isPrivate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        // Güncelleme başarılı, listeyi yenile
                        // Hangi sekmedeysek onu yenilesek daha iyi ama şimdilik getMyLists
                        getMyLists()
                    },
                    { error ->
                        _listsState.value = MyListsState.Error("Hata: ${error.message}")
                    }
                )
        )
    }

    // KEŞFET
    fun getDiscoverLists() {
        _listsState.value = MyListsState.Loading
        disposable.add(
            repository.getPublicLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { lists ->
                        if (lists.isEmpty()) _listsState.value = MyListsState.Empty
                        else _listsState.value = MyListsState.Success(lists)
                    },
                    { error ->
                        _listsState.value =
                            MyListsState.Error("Keşfet yüklenemedi: ${error.message}")
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

sealed class MyListsState {
    object Loading : MyListsState()
    object Empty : MyListsState()
    data class Success(val data: List<CustomListDto>) : MyListsState()
    data class Error(val message: String) : MyListsState()
}