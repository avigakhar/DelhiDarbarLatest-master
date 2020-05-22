package com.app.delhidarbar.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitUtils {
   // val BASE_URL = "http://delhi.uniquecaryantra.com"
    val BASE_URL = "https://api.delhidarbar.es"
    private var retrofitUtils: Retrofit? = null

    fun getRetrofitUtils(): Retrofit? {
        if (retrofitUtils == null) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).readTimeout((100*2000).toLong(),TimeUnit.MILLISECONDS).build()
            retrofitUtils = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }

        return retrofitUtils
    }

}
