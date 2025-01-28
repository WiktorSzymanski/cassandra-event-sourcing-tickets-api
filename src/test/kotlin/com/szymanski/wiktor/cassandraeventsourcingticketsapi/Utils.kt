package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Response

class Utils {
    fun generateUsername(): String {
        val usernames = arrayListOf<String>("Ed", "Ted", "Zed", "Boris", "Michael", "John", "Paul", "Stephen", "Jimmy",
            "Tommy", "Frank", "Michelle", "Amy", "Angelica", "Anastasia", "Martha", "Jolene", "Crystal", "Donald", "Rudolph",
            "Peter", "Meg", "Louise", "Chris", "Brian", "Meg", "Homer", "Bart", "Lisa", "Marge", "Philip", "Hubert")

        return usernames[(0..<usernames.count()).random()]
    }

    fun checkValidity(response : Response<ResponseBody>){
        if (!response.isSuccessful){
            println(response.errorBody()?.string())
            throw Exception(response.message())
        }
    }

    fun containsValue(json : JSONArray, key : String, value : String) : Boolean {
        for (i in 0 until json.length()){
            val obj = json.getJSONObject(i)
            if (obj.has(key) and obj.getString(key).equals(value)) {
                return true
            }
        }

        return false
    }

    fun containsValue(json : JSONArray, key : String, value : Int) : Boolean {
        for (i in 0 until json.length()){
            val obj = json.getJSONObject(i)
            if (obj.has(key) and (obj.getInt(key) == value)) {
                return true
            }
        }

        return false
    }

    fun containsValues(json : JSONArray, key1 : String, value1 : Int, key2 : String, value2 : Int) : Boolean {
        for (i in 0 until json.length()){
            val obj = json.getJSONObject(i)
            if (obj.has(key1) and obj.has(key2) and (obj.getInt(key1) == value1) and (obj.getInt(key2) == value2)) {
                return true
            }
        }

        return false
    }

    fun getJsonArray(response : Response<ResponseBody>) : JSONArray {
        return JSONArray(response.body()!!.string())
    }

    fun generateEventName(): String {
        val genres = arrayListOf<String>("Metal", "Rock", "Jazz", "Classical", "Punk", "HipHop", "Techno", "Electro",
            "Blues", "Pop", "Indie")

        val events = arrayListOf<String>("Concert", "Festival", "Recital", "Battle", "Sing-along", "Performance",
            "Gig", "Jam session", "Show")

        return generateUsername() + "s' " +
                genres[(0..<genres.count()).random()] + " " +
                events[(0..<events.count()).random()]

    }
}