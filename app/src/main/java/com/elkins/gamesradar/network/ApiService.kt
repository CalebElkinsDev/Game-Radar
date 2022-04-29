package com.elkins.gamesradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


private const val CONNECT_TIMEOUT: Long = 15
private const val READ_TIMEOUT: Long = 15
private const val BASE_URL = "https://www.giantbomb.com/api/"

private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private val client = OkHttpClient().newBuilder()
    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
    .build()

interface ApiServices {

    @GET("games")
    suspend fun getAllGames(
        @Query("api_key") apikey: String,
        @Query("format") format: String = "json",
        @Query("filter") filter: String,
        @Query("sort") sort: String = "asc",
        @Query("offset") offset: Int = 0
    ): Response<GamesResponse>

    @GET("game/{guid}")
    suspend fun getGameById(
        @Path("guid") guid: String,
        @Query("api_key") apikey: String,
        @Query("format") format: String = "json",
    ): Response<GameResponse>
}

object GiantBombApi {
    val retrofitService: ApiServices by lazy {
        retrofit.create(ApiServices::class.java)
    }
}