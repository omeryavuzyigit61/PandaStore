package com.allmoviedatabase.pandastore.di

import com.allmoviedatabase.pandastore.data.repository.AuthRepositoryImpl
import com.allmoviedatabase.pandastore.domain.repository.AuthRepository
import com.allmoviedatabase.pandastore.repository.AddressRepository
import com.allmoviedatabase.pandastore.repository.AddressRepositoryImpl
import com.allmoviedatabase.pandastore.repository.ProductRepository
import com.allmoviedatabase.pandastore.repository.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(
        addressRepositoryImpl: AddressRepositoryImpl
    ): AddressRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        movieRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository


}