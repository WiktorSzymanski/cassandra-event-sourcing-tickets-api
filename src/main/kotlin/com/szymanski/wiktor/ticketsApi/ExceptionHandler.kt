package com.szymanski.wiktor.ticketsApi

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(InvalidSeatException::class)
    fun handleInvalidSeatException(ex: InvalidSeatException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoSuchConcertException::class)
    fun handleInvalidSeatException(ex: NoSuchConcertException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(SeatTakenException::class)
    fun handleSeatTakenException(ex: SeatTakenException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(SeatCannotBeReleasedException::class)
    fun handleSeatTakenException(ex: SeatCannotBeReleasedException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.FORBIDDEN)
    }
}
