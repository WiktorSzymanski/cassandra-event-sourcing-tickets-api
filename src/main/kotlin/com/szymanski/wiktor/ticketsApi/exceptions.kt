package com.szymanski.wiktor.ticketsApi

import java.util.*

class InvalidSeatException(row: Int, seat: Int) : RuntimeException("Seat $row row $seat seat is non existing!")

class NoSuchConcertException(id: UUID) : RuntimeException("Concert with id $id could not be found!")

class SeatTakenException(row: Int, seat: Int) : RuntimeException("Seat $row row $seat is taken!")

class SeatCannotBeReleasedException(row: Int, seat: Int) : RuntimeException("Seat $row row $seat cannot be released!")