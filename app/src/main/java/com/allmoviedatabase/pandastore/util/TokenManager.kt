package com.allmoviedatabase.pandastore.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private var prefs: SharedPreferences = context.getSharedPreferences("panda_store_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val KEY_REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val KEY_USER_ID = "USER_ID" // Yeni eklenen
    }

    // 1. LOGIN OLUNCA BUNU KULLAN (Hepsini kaydeder)
    fun saveSession(accessToken: String, refreshToken: String, userId: String) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, accessToken)
        editor.putString(KEY_REFRESH_TOKEN, refreshToken)
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    // 2. TOKEN YENİLENİNCE (Authenticator) BUNU KULLAN (Sadece tokenları günceller, ID kalır)
    fun saveTokens(accessToken: String, refreshToken: String) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, accessToken)
        editor.putString(KEY_REFRESH_TOKEN, refreshToken)
        // User ID'ye dokunmuyoruz, o zaten var.
        editor.apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun clearTokens() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}