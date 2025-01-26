package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface TicketsAPI {

    @GET("/concerts")
    fun getConcerts(): Call<ResponseBody>

    @FormUrlEncoded
    @GET("/concert/{id}")
    fun getConcert(@Field("id") id : String): Call<ResponseBody>

    @FormUrlEncoded
    @GET("/concert/{id}/seats")
    fun getSeats(@Field("id") id : String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/concerts")
    fun createConcert(@Field("name") name : String,
                    @Field("rows") rows : Int,
                    @Field("seats") seats : Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/concert/{id}/seats/release")
    fun releaseSeat(@Field("id") id : String,
                    @Field("row") row : Int,
                    @Field("seat") seat : Int,
                    @Field("username") username : String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/concert/{id}/seats/reserve")
    fun reserveSeat(@Field("id") id : String,
                    @Field("row") row : Int,
                    @Field("seat") seat : Int,
                    @Field("username") username : String): Call<ResponseBody>

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