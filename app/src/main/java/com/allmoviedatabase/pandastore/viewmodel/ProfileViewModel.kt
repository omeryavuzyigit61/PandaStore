package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.domain.repository.AuthRepository
import com.allmoviedatabase.pandastore.model.register.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val userProfile = MutableLiveData<AuthState<UserDto>>()
    private val disposable = CompositeDisposable()

    fun getProfile() {
        userProfile.value = AuthState.Loading
        disposable.add(
            authRepository.getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { user -> userProfile.value = AuthState.Success(user) },
                    { error -> userProfile.value = AuthState.Error(error.message ?: "Hata") }
                )
        )
    }

    fun logout() {
        authRepository.logout()
    }

    // Yaş Hesaplama Yardımcısı
    fun calculateAge(birthDateTimestamp: Long?): String {
        if (birthDateTimestamp == null) return "Yaş: --"

        val dob = Calendar.getInstance().apply { timeInMillis = birthDateTimestamp }
        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return "Yaş: $age"
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}