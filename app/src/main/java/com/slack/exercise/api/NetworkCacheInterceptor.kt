package com.slack.exercise.api

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

private const val CACHE_CONTROL = "Cache-Control"
private const val MAX_CACHE_AGE_MINUTES = 1

/**
 * Implementation of [Interceptor] to be used as a network interceptor for
 * specifying caching policies.
 */
class NetworkCacheInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        var cacheControl = CacheControl.Builder()
            .maxAge(MAX_CACHE_AGE_MINUTES, TimeUnit.MINUTES)
            .build()

        return response.newBuilder()
            .header(CACHE_CONTROL, cacheControl.toString())
            .build()
    }
}
