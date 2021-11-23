package com.slack.exercise.api

import android.content.Context
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val API_URL = "https://slack-users.herokuapp.com/"
private const val CACHE_SIZE = 5 * 1024 * 1024 // 5MB
private const val HTTP_CACHE = "http-cache"

/**
 * Implementation of [SlackApi] using [UserSearchService] to perform the API requests.
 */
@Singleton
class SlackApiImpl @Inject constructor(context: Context) : SlackApi {
  private val service: UserSearchService

  init {
    val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }

    val cache = Cache(
      File(context.cacheDir, HTTP_CACHE),
      CACHE_SIZE.toLong()
    )

    val client: OkHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .addNetworkInterceptor(NetworkCacheInterceptor())
        .addInterceptor(loggingInterceptor)
        .build()

    service = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(client)
        .build()
        .create(UserSearchService::class.java)
  }

  override fun searchUsers(searchTerm: String): Single<List<User>> {
    return service.searchUsers(searchTerm)
        .map {
          it.users
        }
        .subscribeOn(Schedulers.io())

  }
}