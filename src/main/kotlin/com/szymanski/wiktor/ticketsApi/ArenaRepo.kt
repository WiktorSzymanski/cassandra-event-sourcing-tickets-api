package com.szymanski.wiktor.ticketsApi

import org.springframework.stereotype.Service
import java.util.*

@Service
class ArenaRepo(
    private val eventStore: EventStore
) {
    fun load(id: UUID): Arena {
        val events: List<ArenaDomainEvent> = eventStore.loadEvents(id)
        val arena = Arena()
        events.forEach(arena::apply)
        arena.eventToCompensate.toList().forEach {
            val compensationEvent = arena.compensate(it)
            eventStore.saveEvent(id, compensationEvent)
        }
        return arena
    }

    fun createArena(id: UUID, rows: Int, seats: Int): Arena {
        val event = ArenaPreparedEvent(UUID.randomUUID(), 0, rows, seats)
        val arena = load(id)
        arena.apply(event)
        eventStore.saveEvent(id, event)

        return arena
    }

    fun reserveSeat(id: UUID, rows: Int, seat: Int, username: String): Unit {
        val arena = load(id)
        val event = SeatReservedEvent(UUID.randomUUID(), arena.version + 1, rows, seat, username)
        arena.apply(event)
        eventStore.saveEvent(id, event)
    }

    fun releaseSeat(id: UUID, row: Int, seat: Int, username: String): Unit {
        val arena = load(id)
        val event = SeatReleasedEvent(UUID.randomUUID(), arena.version + 1, row, seat, username)
        arena.apply(event)
        eventStore.saveEvent(id, event)
    }
}