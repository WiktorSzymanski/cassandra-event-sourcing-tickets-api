package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import org.junit.jupiter.api.Test

class CassandraEventSourcingTicketsApiApplicationTests {

    private val utils = Utils()

    @Test
    fun createConcert() {
        val api = TicketsAPI.create("http://localhost:8080")

        var response = api.createConcert(utils.generateEventName(), (10..1000).random(), (5..70).random()).execute()
        utils.checkValidity(response)

        val concertId = response.body()!!.string()

        response = api.getConcerts().execute()
        utils.checkValidity(response)
        assert(utils.containsValue(utils.getJsonArray(response), "id", concertId))
    }

    @Test
    fun reserveSeat(){
        val api = TicketsAPI.create("http://localhost:8080")

        var response = api.getConcerts().execute()
        utils.checkValidity(response)

        val concerts = utils.getJsonArray(response)
        if (concerts.length() == 0)
            return

        val concertObj = concerts.getJSONObject((0..<concerts.length()).random())
        val concertId = concertObj.getString("id")

        response = api.getSeats(concertId).execute()
        utils.checkValidity(response)

        val seats = utils.getJsonArray(response)
        val seatObj = seats.getJSONObject((0..<seats.length()).random())
        val row = seatObj.getInt("row")
        val seat = seatObj.getInt("seat")
        val username = utils.generateUsername()

        response = api.reserveSeat(concertId, row, seat, username).execute()
        utils.checkValidity(response)
        val reservationId = response.body()!!.string()

        response = api.getUsersReservations(username).execute()
        utils.checkValidity(response)
        assert(utils.containsValue(utils.getJsonArray(response), "id", reservationId))

        response = api.getSeats(concertId).execute()
        utils.checkValidity(response)
        assert(!utils.containsValues(utils.getJsonArray(response), "row", row, "seat", seat))
    }

    @Test
    fun releaseSeat(){
        val api = TicketsAPI.create("http://localhost:8080")

        val username = utils.generateUsername()

        var response = api.getUsersReservations(username).execute() //TODO - dodac taki endpoint
        utils.checkValidity(response)

        val reservations = utils.getJsonArray(response)
        if (reservations.length() == 0)
            return

        val reservationObj = reservations.getJSONObject((0..<reservations.length()).random())
        val concertId = reservationObj.getString("concert_id")
        val row = reservationObj.getInt("row")
        val seat = reservationObj.getInt("seat")
        val reservationId = reservationObj.getInt("id")

        response = api.releaseSeat(concertId, row, seat, username).execute()
        utils.checkValidity(response)

        response = api.getUsersReservations(username).execute()
        utils.checkValidity(response)
        assert(!utils.containsValue(utils.getJsonArray(response), "reservation_id", reservationId))

        response = api.getSeats(concertId).execute()
        utils.checkValidity(response)
        assert(utils.containsValues(utils.getJsonArray(response), "row", row, "seat", seat))
    }

}
