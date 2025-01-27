package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CassandraEventSourcingTicketsApiApplicationTests {

    private val utils = Utils()

    private val apiAddr = "http://localhost:8080"

    @Test
    fun createConcert() {
        val api = TicketsAPI.create(apiAddr)

        var response = api.createConcert(utils.generateEventName(), (5..25).random(), (10..100).random()).execute()
        utils.checkValidity(response)

        val concertId = response.body()!!.string()

        response = api.getConcerts().execute()
        utils.checkValidity(response)
        assert(utils.containsValue(utils.getJsonArray(response), "id", concertId))
    }

    @Test
    fun reserveSeat(){
        val api = TicketsAPI.create(apiAddr)

        var response = api.getConcerts().execute()
        utils.checkValidity(response)

        val concerts = utils.getJsonArray(response)
        if (concerts.length() == 0)
            return

        val concertObj = concerts.getJSONObject((0..<concerts.length()).random())
        val concertId = concertObj.getString("id")

        response = api.getFreeSeats(concertId).execute()
        utils.checkValidity(response)

        val seats = utils.getJsonArray(response)

        if (seats.length() == 0)
            return

        val seatObj = seats.getJSONObject((0..<seats.length()).random())
        val row = seatObj.getInt("row")
        val seat = seatObj.getInt("seat")
        val username = utils.generateUsername()

        response = api.reserveSeat(concertId, row, seat, username).execute()
        utils.checkValidity(response)

        response = api.getMySeats(concertId, username).execute()
        utils.checkValidity(response)
        assert(utils.containsValues(utils.getJsonArray(response), "row",  row, "seat", seat))

        response = api.getFreeSeats(concertId).execute()
        utils.checkValidity(response)
        assert(!utils.containsValues(utils.getJsonArray(response), "row", row, "seat", seat))
    }

    @Test
    fun releaseSeat(){
        val api = TicketsAPI.create(apiAddr)

        var response = api.getConcerts().execute()
        utils.checkValidity(response)

        val concerts = utils.getJsonArray(response)
        if (concerts.length() == 0)
            return

        val concertObj = concerts.getJSONObject((0..<concerts.length()).random())
        val concertId = concertObj.getString("id")

        val username = utils.generateUsername()

        response = api.getMySeats(concertId, username).execute()
        utils.checkValidity(response)

        val reservations = utils.getJsonArray(response)
        if (reservations.length() == 0)
            return

        val reservationObj = reservations.getJSONObject((0..<reservations.length()).random())
        val row = reservationObj.getInt("row")
        val seat = reservationObj.getInt("seat")

        response = api.releaseSeat(concertId, row, seat, username).execute()
        utils.checkValidity(response)

        response = api.getMySeats(concertId, username).execute()
        utils.checkValidity(response)
        assert(!utils.containsValues(utils.getJsonArray(response), "row", row, "seat", seat))

        response = api.getFreeSeats(concertId).execute()
        utils.checkValidity(response)
        assert(utils.containsValues(utils.getJsonArray(response), "row", row, "seat", seat))
    }

    @Test
    fun stressTest(){
        runBlocking<Unit> {
            val createJobs = List(2) {
                launch {
                    createConcert()
                }
            }

            val reserveJobs = List(5000) {
                launch {
                    reserveSeat()
                }
            }

            val releaseJobs = List(1000) {
                launch {
                    releaseSeat()
                }
            }

            reserveJobs.forEach { it.join() }
            releaseJobs.forEach { it.join() }
            createJobs.forEach { it.join() }
        }
    }

}
