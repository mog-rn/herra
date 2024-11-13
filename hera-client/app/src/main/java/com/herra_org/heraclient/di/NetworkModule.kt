package com.herra_org.heraclient.di

import com.herra_org.heraclient.utils.Constants
import com.herra_org.heraclient.data.remote.api.AuthApi
import com.herra_org.heraclient.data.remote.interceptor.AuthInterceptor
import com.herra_org.heraclient.data.remote.interceptor.RequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.herra_org.heraclient.data.local.TokenManager
import com.herra_org.heraclient.data.remote.api.CycleApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRequestInterceptor(
        tokenManager: TokenManager
    ): RequestInterceptor = RequestInterceptor(tokenManager)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        requestInterceptor: RequestInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(requestInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}