package com.modula.di

import com.modula.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        //Timeout
        httpClient.readTimeout(30, TimeUnit.SECONDS)
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
        httpClient.writeTimeout(30, TimeUnit.SECONDS)


        val interceptorBody = HttpLoggingInterceptor()
        interceptorBody.setLevel(HttpLoggingInterceptor.Level.BODY)

        val interceptor =
            Interceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("x-requested-with", "XMLHttpRequest")
                    .addHeader("accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()

                return@Interceptor chain.proceed(request)
            }

        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(interceptorBody)
        return httpClient.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}