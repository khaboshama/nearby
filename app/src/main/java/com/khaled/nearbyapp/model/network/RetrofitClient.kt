package com.khaled.nearbyapp.model.network

import com.khaled.nearbyapp.constant.Constants
import com.khaled.nearbyapp.model.network.endPoints.VenuesApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


object RetrofitClient {

    val moneyServiceApi: VenuesApi by lazy { getRetrofit().create(VenuesApi::class.java) }

    private fun getRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            val requestBuilder: Request.Builder = chain.request().newBuilder()
            requestBuilder.addHeader("Host", "api.foursquare.com")
            requestBuilder.addHeader("Connection", "keep-alive")
            chain.proceed(requestBuilder.build())
        })
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .client(httpClient.build())
            .build()
    }
}