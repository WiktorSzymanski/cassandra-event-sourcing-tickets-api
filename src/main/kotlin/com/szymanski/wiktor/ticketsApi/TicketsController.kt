package com.szymanski.wiktor.ticketsApi

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

    @GetMapping("/concert/{id}/seats")
    fun getConcertSeats(@PathVariable("id") id: UUID): String = constructArenaString(arenaRepo.load(concertService.getConcertById(id).arena_id).seats)


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

    fun constructArenaString(seats: Array<Array<Seat>>): String {
        val rows = seats.size
        val columns = seats[0].size
        val builder = StringBuilder()

        builder.append("\t")
        for (col in 0 until columns) {
            builder.append(" $col\t")
        }
        builder.append("\n")

        for (row in 0 until rows) {
            builder.append("$row\t")
            for (col in 0 until columns) {
                builder.append(if (seats[row][col].username != null) " X\t" else "[ ]\t")
            }
            builder.append("\n")
        }
        return builder.toString()
    }
}