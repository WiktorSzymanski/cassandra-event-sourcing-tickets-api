package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CassandraEventSourcingTicketsApiApplicationTests(addr : String) {

    private val utils = Utils()

    private val apiAddr : String
    private val api : TicketsAPI

    init{
        this.apiAddr = addr
        this.api = TicketsAPI.create(apiAddr)
    }

    fun createConcert() {
        var response = api.createConcert(utils.generateEventName(), (5..25).random(), (10..100).random()).execute()
        utils.checkValidity(response)

        val concertId = response.body()!!.string()

        response = api.getConcerts().execute()
        utils.checkValidity(response)
        assert(utils.containsValue(utils.getJsonArray(response), "id", concertId))
    }

    fun reserveSeat(){
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

    fun releaseSeat(){
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
}

fun main(args : Array<String>){

    if (args.size < 5){
        println("Wrong number of arguments!")
        println("Needed arguments in order: apiAddress numberOfEventsToCreate numberOfSeatReservations numberOfSeatReleases" )
        return
    }

    runBlocking {
        val tst = CassandraEventSourcingTicketsApiApplicationTests(args[1])
        val createJobs = List(args[2].toInt()) {
            launch {
                tst.createConcert()
            }
        }

        val reserveJobs = List(args[3].toInt()) {
            launch {
                tst.reserveSeat()
            }
        }

        val releaseJobs = List(args[4].toInt()) {
            launch {
                tst.releaseSeat()
            }
        }

        reserveJobs.forEach { it.join() }
        releaseJobs.forEach { it.join() }
        createJobs.forEach { it.join() }
    }
}
