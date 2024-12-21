package com.szymanski.wiktor.ticketsApi

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CassandraEventStore(private val repository: ArenaEventRepository) : EventStore  {

    private val objectMapper = jacksonObjectMapper()

    override fun saveEvent(arenaId: UUID, event: ArenaDomainEvent): Unit {
        val eventType = event::class.simpleName ?: "UnknownEvent"
        val eventData = objectMapper.writeValueAsString(event)
        val eventEntity = ArenaEventEntity(arenaId, eventType, eventData)
        repository.save(eventEntity)
        println("Event saved: $arenaId")
    }

    override fun loadEvents(arenaId: UUID): List<ArenaDomainEvent> {
        val eventEntities = repository.findAllById(listOf(arenaId))
        return eventEntities.mapNotNull { entity ->
            val eventClass = when (entity.eventType) {
                "ArenaPreparedEvent" -> ArenaPreparedEvent::class.java
                "SeatReservedEvent" -> SeatReservedEvent::class.java
                "SeatReleasedEvent" -> SeatReleasedEvent::class.java
                else -> null
            }
            eventClass?.let { objectMapper.readValue(entity.eventData, it) as ArenaDomainEvent }
        }
    }
}
