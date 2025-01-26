package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

class CassandraEventSourcingTicketsApiApplicationTests {

    @Test
        fun reserveSeat() {
            val api = TicketsAPI.create("http://localhost:8080")
            val response = api.getConcerts().execute()
            val body = response.body()
            val code = response.code()
    }

}
