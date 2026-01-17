package com.allmoviedatabase.pandastore.di

import com.allmoviedatabase.pandastore.data.remote.ApiService
import com.allmoviedatabase.pandastore.data.remote.TokenAuthenticator // Eklendi
import com.allmoviedatabase.pandastore.util.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // URL'i tek bir yerden yönetmek için sabitledim
    //192.168.56.1
    private const val BASE_URL = "http://10.0.2.2:3000/" // Gerçek cihazda IP adresini yaz

    @Provides
    @Singleton
    fun provideBaseUrl() = BASE_URL

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val token: String? = tokenManager.getAccessToken()
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
    }

    // --- YENİ EKLENEN: AUTHENTICATOR PROVIDER ---
    @Provides
    @Singleton
    fun provideTokenAuthenticator(tokenManager: TokenManager): TokenAuthenticator {
        return TokenAuthenticator(tokenManager, BASE_URL)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        authenticator: TokenAuthenticator // Buraya enjekte ediyoruz
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(authenticator) // İŞTE SİHİRLİ SATIR BURASI!
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}