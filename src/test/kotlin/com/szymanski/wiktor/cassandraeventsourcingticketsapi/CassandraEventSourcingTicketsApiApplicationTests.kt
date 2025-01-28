package com.szymanski.wiktor.cassandraeventsourcingticketsapi

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Thread.sleep
import kotlin.time.measureTime

class CassandraEventSourcingTicketsApiApplicationTests(addr : String) {

    private val utils = Utils()
    private val apiAddr : String = addr
    private val api : TicketsAPI = TicketsAPI.create(apiAddr)
    private val releaseTimes : MutableList<Long> = mutableListOf()
    private val reserveTimes : MutableList<Long> = mutableListOf()
    private val createTimes : MutableList<Long> = mutableListOf()
    private val getConcertTimes : MutableList<Long> = mutableListOf()
    private val getFreeSeatsTimes : MutableList<Long> = mutableListOf()
    private val getMySeatsTimes : MutableList<Long> = mutableListOf()

    fun printStats(){
        sortTimesLists()
        printListStats("Get my seats", getMySeatsTimes)
        printListStats("Get free seats", getFreeSeatsTimes)
        printListStats("Get concert", getConcertTimes)
        printListStats("Create concert", createTimes)
        printListStats("Release a seat", releaseTimes)
        printListStats("Reserve a seat", reserveTimes)
    }

    private fun sortTimesLists() {
        releaseTimes.sort()
        reserveTimes.sort()
        createTimes.sort()
        getConcertTimes.sort()
        getFreeSeatsTimes.sort()
        getMySeatsTimes.sort()
    }

    private fun printListStats(title : String, list : MutableList<Long>){
        println("$title response times [ms] avg ${list.average()}, min ${list.min()}, max ${list.max()}, " +
                "Q1 ${list[list.size/4]}, Q2 ${list[list.size/2]}, Q3 ${list[list.size/2 + list.size/4]}")
    }

    fun createConcert() {
        val timeTaken = measureTime {
            var response : Response<ResponseBody>

            val createTime = measureTime { response = api.createConcert(utils.generateEventName(), (5..25).random(), (10..100).random()).execute() }
            createTimes.add(createTime.inWholeMilliseconds)
            println("Create concert call $createTime")

            utils.checkValidity(response)

            val concertId = response.body()!!.string()

            val getConcertTime = measureTime { response = api.getConcerts().execute() }
            getConcertTimes.add(getConcertTime.inWholeMilliseconds)
            println("Get concerts call $getConcertTime")

            utils.checkValidity(response)
            assert(utils.containsValue(utils.getJsonArray(response), "id", concertId))
        }

        println("Create test $timeTaken")
    }

    fun reserveSeat(){
        val timeTaken = measureTime {
            var response : Response<ResponseBody>

            val getConcertTime = measureTime { response = api.getConcerts().execute() }
            getConcertTimes.add(getConcertTime.inWholeMilliseconds)
            println("Get concerts call $getConcertTime")
            utils.checkValidity(response)

            val concerts = utils.getJsonArray(response)
            if (concerts.length() == 0)
                return

            val concertObj = concerts.getJSONObject((0..<concerts.length()).random())
            val concertId = concertObj.getString("id")

            var getFreeSeatsTime = measureTime { response = api.getFreeSeats(concertId).execute() }
            getFreeSeatsTimes.add(getFreeSeatsTime.inWholeMilliseconds)
            println("Get free seats call $getFreeSeatsTime")
            utils.checkValidity(response)

            val seats = utils.getJsonArray(response)

            if (seats.length() == 0)
                return

            val seatObj = seats.getJSONObject((0..<seats.length()).random())
            val row = seatObj.getInt("row")
            val seat = seatObj.getInt("seat")
            val username = utils.generateUsername()

            val reserveSeatTime = measureTime { response = api.reserveSeat(concertId, row, seat, username).execute() }
            reserveTimes.add(reserveSeatTime.inWholeMilliseconds)
            println("Reserve seat call $reserveSeatTime")
            utils.checkValidity(response)

            val getMySeatsTime = measureTime { response = api.getMySeats(concertId, username).execute() }
            getMySeatsTimes.add(getMySeatsTime.inWholeMilliseconds)
            println("Get my seats call $getMySeatsTime")
            utils.checkValidity(response)
            assert(utils.containsValues(utils.getJsonArray(response), "row", row, "seat", seat))

            getFreeSeatsTime = measureTime { response = api.getFreeSeats(concertId).execute() }
            getFreeSeatsTimes.add(getFreeSeatsTime.inWholeMilliseconds)
            println("Get free seats call $getFreeSeatsTime")
            utils.checkValidity(response)
            assert(!utils.containsValues(utils.getJsonArray(response), "row", row, "seat", seat))
        }

        println("Reserve test $timeTaken")
    }

    fun releaseSeat(){
        val timeTaken = measureTime {
            var response : Response<ResponseBody>

            val getConcertTime = measureTime { response = api.getConcerts().execute() }
            getConcertTimes.add(getConcertTime.inWholeMilliseconds)
            println("Get concerts call $getConcertTime")
            utils.checkValidity(response)

            val concerts = utils.getJsonArray(response)
            if (concerts.length() == 0)
                return

            val concertObj = concerts.getJSONObject((0..<concerts.length()).random())
            val concertId = concertObj.getString("id")

            val username = utils.generateUsername()

            var getMySeatsTime = measureTime { response = api.getMySeats(concertId, username).execute() }
            getMySeatsTimes.add(getMySeatsTime.inWholeMilliseconds)
            println("Get my seats call $getMySeatsTime")
            utils.checkValidity(response)

            val reservations = utils.getJsonArray(response)
            if (reservations.length() == 0)
                return

            val reservationObj = reservations.getJSONObject((0..<reservations.length()).random())
            val row = reservationObj.getInt("row")
            val seat = reservationObj.getInt("seat")

            val releaseTime = measureTime { response = api.releaseSeat(concertId, row, seat, username).execute() }
            releaseTimes.add(releaseTime.inWholeMilliseconds)
            println("Release seat call $releaseTime")
            utils.checkValidity(response)

            getMySeatsTime = measureTime { response = api.getMySeats(concertId, username).execute() }
            getMySeatsTimes.add(getMySeatsTime.inWholeMilliseconds)
            println("Get my seats call $getMySeatsTime")
            utils.checkValidity(response)
            assert(!utils.containsValues(utils.getJsonArray(response), "row", row, "seat", seat))

            val getFreeSeatsTime = measureTime { response = api.getFreeSeats(concertId).execute() }
            getFreeSeatsTimes.add(getFreeSeatsTime.inWholeMilliseconds)
            println("Get free seats call $getFreeSeatsTime")
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

            tst.printStats()
        }
    }

    println("Whole operation took ${timeTaken}")


}
