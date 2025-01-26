package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

interface TicketsAPI {

    @GET("/concerts")
    fun getConcerts(): Call<ResponseBody>

    @GET("/concert/{id}")
    fun getConcert(): Call<ResponseBody>

    @GET("/concert/{id}/seats")
    fun getSeats(): Call<ResponseBody>

    @POST("/concert/{id}/seats/reserve")
    fun reserveSeat(): Call<ResponseBody>

    @POST("/concert/{id}/seats/release")
    fun releaseSeat(): Call<ResponseBody>

    @POST("/concerts")
    fun createConcert(): Call<ResponseBody>

    companion object {
        fun create(baseUrl: String): TicketsAPI {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(TicketsAPI::class.java)
        }
    }
}