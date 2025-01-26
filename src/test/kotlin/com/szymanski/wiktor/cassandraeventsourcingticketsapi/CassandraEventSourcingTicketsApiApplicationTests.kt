package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

class CassandraEventSourcingTicketsApiApplicationTests {

    @Test
    fun createConcert() {
        val api = TicketsAPI.create("http://localhost:8080")
        var response = api.getConcerts().execute()

        assert(response.isSuccessful)

        val json = JSONArray(response.body()!!.string())
        val length = json.length()

        response = api.createConcert("abc", 1, 1).execute()

        assert(response.isSuccessful)

        val id = response.body()!!.string()

        response = api.getConcerts().execute()

        val json2 = JSONArray(response.body()!!.string())
        val length2 = json2.length()

        assert(length2 > length)

        var containsId = false
        for (i in 0 until length2){
            val obj = json2.getJSONObject(i)
            if (obj.has("id") and obj.getString("id").equals(id)){
                containsId = true
            }
        }

        assert(containsId)
    }


}
