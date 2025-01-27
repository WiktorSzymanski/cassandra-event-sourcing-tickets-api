package com.szymanski.wiktor.ticketsApi

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController("/api")
class TicketsController(
    private val arenaRepo: ArenaRepo,
    private val concertService: ConcertService,
) {
//    private final val arenaRepo = ArenaRepo(FileEventStore("./.event-store"))

    @GetMapping("/concerts")
    fun getConcerts(): List<Concert> {
        return concertService.getConcerts()
    }

    @GetMapping("/concert/{id}")
    fun getConcert(@PathVariable id: UUID): Concert {
        return concertService.getConcertById(id)
    }

    @PostMapping("/concerts")
    fun createConcert(@RequestParam("name") name: String, @RequestParam("rows") rows: Int, @RequestParam("seats") seats: Int): String {
        val concert = concertService.createConcert(name)
        arenaRepo.createArena(concert.arena_id, rows, seats)
        return concert.id
    }

    @GetMapping("/concert/{id}/free_seats")
    fun getConcertSeats(@PathVariable("id") id: UUID): String = getFreeSeats(arenaRepo.load(concertService.getConcertById(id).arena_id).seats)

    @GetMapping("/concert/{id}/my_seats/{username}")
    fun getReservedSeats(@PathVariable("id") id: UUID, @PathVariable("username") username: String): String {
        return getSeatsReservedBy(arenaRepo.load(concertService.getConcertById(id).arena_id).seats, username)
    }

    @PostMapping("/concert/{id}/seats/reserve")
    fun reserveSeat(@PathVariable("id") id: UUID, @RequestParam("row") row: Int, @RequestParam("seat") seat: Int,  @RequestParam("username") username: String): Boolean {
        val concert = concertService.getConcertById(id)
        arenaRepo.reserveSeat(concert.arena_id, row, seat, username)
        return true
    }

    @PostMapping("/concert/{id}/seats/release")
    fun releaseSeat(@PathVariable("id") id: UUID, @RequestParam("row") row: Int, @RequestParam("seat") seat: Int,  @RequestParam("username") username: String): Boolean {
        arenaRepo.releaseSeat(concertService.getConcertById(id).arena_id, row, seat, username)
        return true
    }

    fun getSeatsReservedBy(seats: Array<Array<Seat>>, username : String): String {
        val rows = seats.size
        val columns = seats[0].size

        val seatsArr = JsonArray()

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                if (seats[row][col].username == username) {
                    val json = JsonObject()
                    json.addProperty("row", row)
                    json.addProperty("seat", col)
                    seatsArr.add(json)
                }
            }
        }
        return seatsArr.toString()
    }

    fun getFreeSeats(seats: Array<Array<Seat>>): String {
        val rows = seats.size
        val columns = seats[0].size

        val seatsArr = JsonArray()

        for (row in 0 until rows) {
            for (col in 0 until columns) {
               if (seats[row][col].username == null) {
                   val json = JsonObject()
                   json.addProperty("row", row)
                   json.addProperty("seat", col)
                   seatsArr.add(json)
               }
            }
        }
        return seatsArr.toString()
    }
}