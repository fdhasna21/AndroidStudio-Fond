package com.fdhasna21.fond.utility.network

import android.content.Context
import com.fdhasna21.fond.R
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Fernanda Hasna on 26/10/2024.
 */

class ServerAPI {
    private var retrofit : Retrofit? = null
    private var httpClient = OkHttpClient.Builder()

    fun getServerAPI(context : Context) : Retrofit {
        if(retrofit == null){
            httpClient.addInterceptor(Interceptor { chain ->
                val original: Request = chain.request()
                val request = original.newBuilder()
                    .header("accept", "application/json")
                    .header("Authorization", context.getString(R.string.auth_foursquare))
                    .method(original.method, original.body)

                    .build()
                chain.proceed(request)
            })

            httpClient.addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val client = httpClient
                .build()
            retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.foursquare.com/v3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }
}