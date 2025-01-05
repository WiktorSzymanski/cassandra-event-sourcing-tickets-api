package com.szymanski.wiktor.ticketsApi

import java.util.UUID

sealed interface ArenaDomainEvent {
    val id: UUID
    val version: Int
}

sealed interface ArenaCompensationEvent : ArenaDomainEvent {
    val compensatesEventId: UUID
}

data class ArenaPreparedEvent(
    override val id: UUID,
    override val version: Int,
    val numberOfRows: Int,
    val numberOfSeats: Int
) : ArenaDomainEvent

class SeatReservedEvent(
    override val id: UUID,
    override val version: Int,
    val row: Int,
    val seat: Int,
    val username: String
) : ArenaDomainEvent

class SeatReleasedEvent(
    override val id: UUID,
    override val version: Int,
    val row: Int,
    val seat: Int,
    val username: String
) : ArenaDomainEvent

class SeatReservedCompensationEvent(
    override val id: UUID,
    override val version: Int,
    override val compensatesEventId: UUID,
) : ArenaCompensationEvent

class SeatReleasedCompensationEvent(
    override val id: UUID,
    override val version: Int,
    override val compensatesEventId: UUID,
) : ArenaCompensationEvent