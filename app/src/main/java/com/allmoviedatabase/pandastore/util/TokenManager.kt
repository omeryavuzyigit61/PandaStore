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

    fun saveTokens(accessToken: String, refreshToken: String) {
        val editor = prefs.edit()
        editor.putString("ACCESS_TOKEN", accessToken)
        editor.putString("REFRESH_TOKEN", refreshToken)
        editor.apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString("ACCESS_TOKEN", null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString("REFRESH_TOKEN", null)
    }

    fun clearTokens() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}