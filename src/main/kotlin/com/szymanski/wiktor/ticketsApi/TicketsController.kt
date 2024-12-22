package com.szymanski.wiktor.ticketsApi

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController("/api")
class TicketsController {
    private final val arenaRepo = ArenaRepo(FileEventStore("./.event-store"))
    val id = UUID.randomUUID()

    @PostMapping("/concerts")
    fun createConcert(): UUID {
        arenaRepo.apply(id, ArenaPreparedEvent(UUID.randomUUID(), 10, 10))
        return id
    }

    @GetMapping("/concert/seats")
    fun getConcertSeats(): String = constructArenaString(arenaRepo.load(id).seats)


    @PostMapping("/concert/seats/reserve")
    fun reserveSeat(@RequestParam("row") row: Int, @RequestParam("seat") seat: Int,  @RequestParam("username") username: String): Boolean {
        arenaRepo.apply(id, SeatReservedEvent(UUID.randomUUID(), row, seat, username))
        return true
    }

    @PostMapping("/concert/seats/release")
    fun releaseSeat(@RequestParam("row") row: Int, @RequestParam("seat") seat: Int,  @RequestParam("username") username: String): Boolean {
        arenaRepo.apply(id, SeatReleasedEvent(UUID.randomUUID(), row, seat))
        return true
    }

    fun constructArenaString(seats: Array<Array<String?>>): String {
        val rows = seats.size
        val columns = seats[0].size
        val builder = StringBuilder()

        builder.append("   ")
        for (col in 1..columns) {
            builder.append(" $col ")
        }
        builder.append("\n")

        for (row in 0 until rows) {
            builder.append("${row + 1} ")
            for (col in 0 until columns) {
                builder.append(if (seats[row][col] != null) " [X] " else " [ ] ")
            }
            builder.append("\n")
        }
        return builder.toString()
    }
}