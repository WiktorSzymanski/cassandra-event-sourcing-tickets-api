package com.szymanski.wiktor.ticketsApi

import java.sql.Timestamp
import java.util.*

interface EventStore {
    fun saveEvent(arenaId: UUID, event: ArenaDomainEvent): ArenaEventEntity
    fun loadEvents(arenaId: UUID): List<ArenaDomainEvent>
    fun loadEventsFromTimestamp(arenaId: UUID, timestamp: String): List<ArenaDomainEvent>
    fun getArenaSnapshot(arenaId: UUID): Pair<Arena, String>
    fun createSnapshot(arenaId: UUID, eventEntity: ArenaEventEntity)
}