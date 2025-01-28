package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import kotlin.time.measureTime

class CassandraEventSourcingTicketsApiApplicationTests(addr : String) {

    private val utils = Utils()
    private val apiAddr : String = addr
    private val api : TicketsAPI = TicketsAPI.create(apiAddr)

    fun createConcert() {
        val timeTaken = measureTime {
            var response = api.createConcert(utils.generateEventName(), (5..25).random(), (10..100).random()).execute()
            utils.checkValidity(response)

            val concertId = response.body()!!.string()

            response = api.getConcerts().execute()
            utils.checkValidity(response)
            assert(utils.containsValue(utils.getJsonArray(response), "id", concertId))
        }

        println("Create test $timeTaken")
    }

    fun reserveSeat(){
        val timeTaken = measureTime {
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
            assert(utils.containsValues(utils.getJsonArray(response), "row", row, "seat", seat))

            response = api.getFreeSeats(concertId).execute()
            utils.checkValidity(response)
            assert(!utils.containsValues(utils.getJsonArray(response), "row", row, "seat", seat))
        }

        println("Reserve test $timeTaken")
    }

    fun releaseSeat(){
        val timeTaken = measureTime {
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

        println("Release test $timeTaken")
    }
}

fun main(args : Array<String>){

    if (args.size < 4){
        println("Wrong number of arguments!")
        println("Needed arguments in order: apiAddress numberOfEventsToCreate numberOfSeatReservations numberOfSeatReleases" )
        return
    }

    println("Running with configuration: " +
            "\n apiAddr = ${args[0]} " +
            "\n number of create jobs = ${args[1]} " +
            "\n number of seat reservations = ${args[2]} " +
            "\n number of seat releases = ${args[3]}")

    val timeTaken = measureTime {
        runBlocking {
            val tst = CassandraEventSourcingTicketsApiApplicationTests(args[0])
            val createJobs = List(args[1].toInt()) {
                launch {
                    tst.createConcert()
                }
            }

            sleep(2000)

            val reserveJobs = List(args[2].toInt()) {
                launch {
                    tst.reserveSeat()
                }
            }

            sleep(15000)

            val releaseJobs = List(args[3].toInt()) {
                launch {
                    tst.releaseSeat()
                }
            }

            reserveJobs.forEach { it.join() }
            releaseJobs.forEach { it.join() }
            createJobs.forEach { it.join() }
        }
    }

    println("Operation took ${timeTaken}")
}
