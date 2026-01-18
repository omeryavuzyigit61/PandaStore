package com.allmoviedatabase.pandastore.di

import com.allmoviedatabase.pandastore.repository.AuthRepositoryImpl
import com.allmoviedatabase.pandastore.repository.CartRepositoryImpl
import com.allmoviedatabase.pandastore.repository.OrderRepositoryImpl
import com.allmoviedatabase.pandastore.repository.AuthRepository
import com.allmoviedatabase.pandastore.repository.OrderRepository
import com.allmoviedatabase.pandastore.repository.AddressRepository
import com.allmoviedatabase.pandastore.repository.AddressRepositoryImpl
import com.allmoviedatabase.pandastore.repository.CartRepository
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

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

}