package com.moviestreamer.di

import com.moviestreamer.BuildConfig
import com.moviestreamer.data.AuthInterceptor
import com.moviestreamer.data.LocaleInterceptor
import com.moviestreamer.data.TmdbApi
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * SHA-256 pins for api.themoviedb.org.
 * Update these when TMDB rotates their TLS certificate.
 * Obtain current pins with:
 *   openssl s_client -connect api.themoviedb.org:443 | \
 *     openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | \
 *     openssl dgst -sha256 -binary | base64
 */
private val tmdbCertificatePinner = CertificatePinner.Builder()
    // Leaf + intermediate pins for api.themoviedb.org (Cloudflare-backed)
    .add("api.themoviedb.org", "sha256/58qRu/uxh4gFezqAcERupSkRYBlBAvfcw7mEjGPLnNU=")
    .add("api.themoviedb.org", "sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI=")
    .build()

val networkModule = module {
    single { AuthInterceptor(apiKey = BuildConfig.TMDB_API_KEY) }
    single { LocaleInterceptor(androidContext()) }

    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<LocaleInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .certificatePinner(tmdbCertificatePinner)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }
}
