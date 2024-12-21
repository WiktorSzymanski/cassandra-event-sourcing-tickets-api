package com.szymanski.wiktor.ticketsApi

class InvalidSeatException(row: Int, seat: Int) : Exception("Seat $row row $seat seat is non existing!")