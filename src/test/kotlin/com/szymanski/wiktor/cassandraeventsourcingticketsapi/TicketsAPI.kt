package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import io.swagger.v3.oas.annotations.Parameter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface TicketsAPI {

    @GET("/concerts")
    fun getConcerts(): Call<ResponseBody>

    @GET("/concert/{id}")
    fun getConcert(@Path("id") id : String): Call<ResponseBody>

    @GET("/concert/{id}/free_seats")
    fun getFreeSeats(@Path("id") id : String): Call<ResponseBody>

    @GET("/concert/{id}/my_seats/{username}")
    fun getMySeats(@Path("id") id : String, @Path("username") username: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/concerts")
    fun createConcert(@Field("name") name : String,
                    @Field("rows") rows : Int,
                    @Field("seats") seats : Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/concert/{id}/seats/release")
    fun releaseSeat(@Path("id") id : String,
                    @Field("row") row : Int,
                    @Field("seat") seat : Int,
                    @Field("username") username : String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/concert/{id}/seats/reserve")
    fun reserveSeat(@Path("id") id : String,
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