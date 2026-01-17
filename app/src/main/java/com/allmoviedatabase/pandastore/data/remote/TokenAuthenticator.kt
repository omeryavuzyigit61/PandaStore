package com.allmoviedatabase.pandastore.data.remote

import com.allmoviedatabase.pandastore.model.login.LoginResponse
import com.allmoviedatabase.pandastore.util.TokenManager
import com.google.gson.Gson
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    // Base URL'i NetworkModule'den alabilirdik ama basitlik için hardcode veya buildconfig yapabilirsin.
    // Şimdilik emulator için elle yazıyorum, sen NetworkModule'deki ile aynı yap.
    private val baseUrl: String = "http://10.0.2.2:3000/"
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. Zaten bir token yenileme denemesi yapılmış mı kontrol et (Sonsuz döngüye girmesin)
        if (response.responseCount >= 3) {
            return null // Pes et
        }

        // 2. Elimizdeki Refresh Token'ı al
        val refreshToken = tokenManager.getRefreshToken() ?: return null

        // 3. Senkron (Anlık) olarak Token Yenileme İsteği at
        val newTokenResponse = refreshTokenCall(refreshToken)

        // 4. Yenileme başarılı mı?
        return if (newTokenResponse != null) {
            // Yeni tokenları kaydet
            tokenManager.saveTokens(newTokenResponse.accessToken, newTokenResponse.refreshToken)

            // 5. BAŞARISIZ OLAN İSTEĞİ YENİ TOKEN İLE TEKRARLA
            response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokenResponse.accessToken}")
                .build()
        } else {
            // Refresh token da patlamışsa (süresi bitmişse) çıkış yap
            tokenManager.clearTokens()
            null
        }
    }

    // OkHttp ile Manuel Refresh İsteği (Retrofit kullanmıyoruz döngü olmasın diye)
    private fun refreshTokenCall(refreshToken: String): LoginResponse? {
        val client = OkHttpClient() // Hafif bir client oluşturuyoruz

        // API Refresh endpointine uygun request
        val request = Request.Builder()
            .url("${baseUrl}auth/refresh")
            .post("".toRequestBody()) // Body boş olabilir veya gerekliyse ekle
            .addHeader("Authorization", "Bearer $refreshToken") // Refresh token genelde header'da istenir
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyString = response.body?.string()
                Gson().fromJson(bodyString, LoginResponse::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private val Response.responseCount: Int
        get() {
            var result = 1
            var prior = priorResponse
            while (prior != null) {
                result++
                prior = prior.priorResponse
            }
            return result
        }
}