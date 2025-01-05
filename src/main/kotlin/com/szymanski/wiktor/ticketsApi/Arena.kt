package com.szymanski.wiktor.ticketsApi

import java.util.*

class Arena {
    lateinit var seats: Array<Array<Seat>>
    lateinit var eventToCompensate: MutableList<ArenaDomainEvent>

    var version: Int = 0

    fun apply(event: ArenaDomainEvent): ArenaDomainEvent  {
        when (event) {
            is ArenaPreparedEvent -> prepareArena(event)
            is SeatReservedEvent -> reserveSeat(event)
            is SeatReleasedEvent -> releaseSeat(event)
            is SeatReservedCompensationEvent -> compensateSeat(event)
            is SeatReleasedCompensationEvent -> compensateSeat(event)
        }
        this.version = event.version
        return event
    }

    fun compensate(event: ArenaDomainEvent): ArenaDomainEvent {
        when (event) {
            is SeatReservedEvent -> {
                println("Compensating seat (${event.row}, ${event.seat}) reserved event")
                return this.apply(SeatReservedCompensationEvent(UUID.randomUUID(), this.version + 1, event.id))
            }
            is SeatReleasedEvent -> {
                println("Compensating seat (${event.row}, ${event.seat}) released event")
                return this.apply(SeatReleasedCompensationEvent(UUID.randomUUID(), this.version + 1, event.id))
            }
            else -> throw UnsupportedOperationException()
        }
    }

    private fun prepareArena(event: ArenaPreparedEvent): Unit {
        this.seats = Array(event.numberOfRows) { Array(event.numberOfSeats) { Seat(null, 0) } }
        this.eventToCompensate = mutableListOf()
        this.version = event.version
    }

    private fun reserveSeat(event: SeatReservedEvent): Unit {
        isSeatValid(event.row, event.seat)

        val seat = seats[event.row][event.seat]
        if (event.version <= seat.version) {
            eventToCompensate.add(event)
            return
        }

        if (seat.username == null) {
            seat.username = event.username
            seat.version = event.version
            println("Seat (${event.row}, ${event.seat}) successfully reserved.")
        } else {
            println("Seat (${event.row}, ${event.seat}) is already reserved.")
            throw SeatTakenException(event.row, event.seat)
        }
        this.version = event.version
    }

    private fun releaseSeat(event: SeatReleasedEvent): Unit {
        isSeatValid(event.row, event.seat)

        val seat = seats[event.row][event.seat]
        if (event.version <= seat.version) {
            eventToCompensate.add(event)
            return
        }
        if (seat.username == event.username) {
            seat.username = null
            seat.version = event.version
            println("Seat (${event.row}, ${event.seat}) successfully released.")
        } else {
            println("Seat (${event.row}, ${event.seat}) could not be released.")
            throw SeatCannotBeReleasedException(event.row, event.seat)
        }
        this.version = event.version
    }

    private fun compensateSeat(event: ArenaCompensationEvent): Unit {
        eventToCompensate.removeIf { event.compensatesEventId == it.id }
    }

    private fun isSeatValid(row: Int, seat: Int): Unit {
        if (row !in 0.until(seats.size) || seat !in 0.until(seats[row].size)) {
            throw InvalidSeatException(row, seat)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Arena
        seats.forEachIndexed { index, row ->
            if (!row.contentEquals(other.seats[index])) return false
        }
        return true
    }

    override fun hashCode(): Int {
        return seats.contentDeepHashCode()
    }
}