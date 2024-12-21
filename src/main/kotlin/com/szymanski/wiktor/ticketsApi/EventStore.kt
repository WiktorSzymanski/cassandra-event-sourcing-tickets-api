package com.szymanski.wiktor.ticketsApi

import java.util.*

interface EventStore {
    fun saveEvent(arenaId: UUID, event: ArenaDomainEvent): Unit
    fun loadEvents(arenaId: UUID): List<ArenaDomainEvent>
}