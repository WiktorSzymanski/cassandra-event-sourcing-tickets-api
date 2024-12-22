package com.szymanski.wiktor.ticketsApi

class Arena {
    lateinit var seats: Array<Array<String?>>

    fun apply(event: ArenaDomainEvent): Unit = when (event) {
        is ArenaPreparedEvent -> prepareArena(event)
        is SeatReservedEvent -> reserveSeat(event)
        is SeatReleasedEvent -> releaseSeat(event)
        else -> {
            println("No such event: $event")
        }
    }

    private fun prepareArena(event: ArenaPreparedEvent): Unit {
        seats = Array(event.numberOfRows) { arrayOfNulls<String?>(event.numberOfSeats) }
    }

    private fun reserveSeat(event: SeatReservedEvent): Unit {
        isSeatValid(event.row, event.seat)

        if (seats[event.row][event.seat] == null) {
            seats[event.row][event.seat] = "Reserved"
            println("Seat (${event.row}, ${event.seat}) successfully reserved.")
        } else {
            println("Seat (${event.row}, ${event.seat}) is already reserved.")
        }
    }

    private fun releaseSeat(event: SeatReleasedEvent): Unit {
        isSeatValid(event.row, event.seat)

        if (seats[event.row][event.seat] == "Reserved") {
            seats[event.row][event.seat] = null
            println("Seat (${event.row}, ${event.seat}) successfully released.")
        } else {
            println("Seat (${event.row}, ${event.seat}) was not reserved.")
        }
    }

    private fun isSeatValid(row: Int, seat: Int): Unit {
        if (row !in 0..seats.size || seat !in 0..seats[row].size) {
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