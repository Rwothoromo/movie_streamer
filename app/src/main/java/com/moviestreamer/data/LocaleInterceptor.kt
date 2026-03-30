package com.moviestreamer.data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

/**
 * OkHttp interceptor that appends the device locale as TMDB's `language` query parameter.
 *
 * TMDB accepts BCP 47 language tags (e.g. "en-US", "es-ES", "fr-FR", "ar").
 * This ensures content metadata (titles, overviews) is returned in the user's language
 * when TMDB has a translation available.
 */
class LocaleInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val configuration = context.resources.configuration
        val locale: Locale = if (android.os.Build.VERSION.SDK_INT >= 24) {
            configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            configuration.locale
        }
        val language = buildString {
            append(locale.language)
            if (locale.country.isNotEmpty()) {
                append('-')
                append(locale.country)
            }
        }
        val request = chain.request().newBuilder()
            .url(
                chain.request().url.newBuilder()
                    .addQueryParameter("language", language)
                    .build()
            )
            .build()
        return chain.proceed(request)
    }
}
