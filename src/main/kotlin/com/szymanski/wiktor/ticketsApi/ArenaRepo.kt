package com.szymanski.wiktor.ticketsApi

import java.util.*

class ArenaRepo(
    private val eventStore: EventStore
) {
    fun load(id: UUID): Arena {
        val events: List<ArenaDomainEvent> = eventStore.loadEvents(id)
        val arena = Arena()
        events.forEach(arena::apply)
        return arena
    }

    fun apply(id: UUID, event: ArenaDomainEvent): Unit {
        val arena = load(id)
        arena.apply(event)
        eventStore.saveEvent(id, event)
    }
}