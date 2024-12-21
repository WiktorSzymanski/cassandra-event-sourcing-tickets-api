package com.szymanski.wiktor.ticketsApi

import java.util.UUID

open class ArenaDomainEvent(id: UUID)

data class ArenaPreparedEvent(
    val id: UUID,
    val numberOfRows: Int,
    val numberOfSeats: Int
) : ArenaDomainEvent(id)

class SeatReservedEvent(
    val id: UUID,
    val row: Int,
    val seat: Int,
    val username: String
) : ArenaDomainEvent(id)

class SeatReleasedEvent(
    val id: UUID,
    val row: Int,
    val seat: Int
) : ArenaDomainEvent(id)