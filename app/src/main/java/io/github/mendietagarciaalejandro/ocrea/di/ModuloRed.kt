package io.github.mendietagarciaalejandro.ocrea.di

import io.github.mendietagarciaalejandro.ocrea.BuildConfig
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.ApiArtic
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuloRed {

    @Provides
    @Singleton
    fun proporcionarJson(): Json = Json {
        // La API añade campos con el tiempo y ademas se piden solo algunos con "fields":
        // sin esto, cualquier clave inesperada tumbaria la deserializacion.
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun proporcionarClienteHttp(): OkHttpClient {
        val registro = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(registro)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun proporcionarRetrofit(cliente: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(ApiArtic.URL_BASE)
        .client(cliente)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun proporcionarApiArtic(retrofit: Retrofit): ApiArtic = retrofit.create(ApiArtic::class.java)
}
