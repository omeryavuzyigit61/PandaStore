package com.allmoviedatabase.pandastore.util

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    // Token'ın içindeki 'sub' (Subject) alanını, yani UserID'yi okur
    fun getUserIdFromToken(token: String): String? {
        try {
            // JWT 3 parçadır: Header.Payload.Signature
            // Bize ortadaki (Payload) lazım
            val parts = token.split(".")
            if (parts.size < 2) return null

            // Base64 şifresini çöz
            val decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            // JSON'a çevir ve 'sub' değerini al
            val jsonObject = JSONObject(decodedString)
            return jsonObject.optString("sub") // 'sub' genelde ID tutar
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}