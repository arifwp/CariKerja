package com.amikom.carikerja.di

import com.amikom.carikerja.api.ApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

//    @Singleton
//    @Provides
//    fun getOkHttpClient(
//        interceptor: Interceptor
//    ): OkHttpClient {
//        val httpBuilder = OkHttpClient.Builder()
//            .addInterceptor(interceptor)
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(50, TimeUnit.SECONDS)
//
//        return httpBuilder
//            .protocols(mutableListOf(Protocol.HTTP_1_1))
//            .build()
//    }

    @Singleton
    @Provides
    fun provideOkHttpClient() = run {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api-stg.oyindonesia.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideFirebaseDatabase() = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthentication() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance()


}