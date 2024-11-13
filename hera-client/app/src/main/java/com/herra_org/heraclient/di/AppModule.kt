package com.herra_org.heraclient.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.herra_org.heraclient.data.local.TokenManager
import com.herra_org.heraclient.data.remote.api.AuthApi
import com.herra_org.heraclient.data.remote.api.CycleApi
import com.herra_org.heraclient.data.remote.api.ProfileApi
import com.herra_org.heraclient.data.remote.repository.AuthRepositoryImpl
import com.herra_org.heraclient.data.remote.repository.ProfileRepositoryImpl
import com.herra_org.heraclient.domain.repository.AuthRepository
import com.herra_org.heraclient.domain.repository.ProfileRepository
import com.herra_org.heraclient.domain.use_cases.auth.LoginUseCase
import com.herra_org.heraclient.domain.use_cases.auth.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager = TokenManager(context)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        firebaseAuth: FirebaseAuth,
        tokenManager: TokenManager
    ): AuthRepository = AuthRepositoryImpl(api, firebaseAuth, tokenManager)

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase = LoginUseCase(repository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase =
        RegisterUseCase(repository)

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi =
        retrofit.create(ProfileApi::class.java)

    @Provides
    @Singleton
    fun provideProfileRepository(api: ProfileApi): ProfileRepository =
        ProfileRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideCycleApi(retrofit: Retrofit): CycleApi {
        return retrofit.create(CycleApi::class.java)
    }
}