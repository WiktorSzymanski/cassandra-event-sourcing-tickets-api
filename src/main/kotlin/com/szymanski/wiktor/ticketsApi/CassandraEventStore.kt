package com.szymanski.wiktor.ticketsApi

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.DelicateCoroutinesApi
import org.springframework.stereotype.Service
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID
import java.time.Instant

@Service
class CassandraEventStore(
    private val repository: ArenaEventRepository,
    private val snapshotRepository: SnapshotRepository) : EventStore  {

    private val objectMapper = jacksonObjectMapper()

    override fun saveEvent(arenaId: UUID, event: ArenaDomainEvent): ArenaEventEntity {
        val eventType = event::class.simpleName ?: "UnknownEvent"
        val eventData = objectMapper.writeValueAsString(event)
        val eventEntity = ArenaEventEntity(arenaId, Instant.now().toString(), eventType, eventData)
        return repository.save(eventEntity)
    }

    override fun getArenaSnapshot(arenaId: UUID): Pair<Arena, String> {
        val snapshotEventRaw = repository.findMostRecentByArenaIdAndEventType(arenaId, "SnapshotEvent")
            ?: return Pair(Arena(), "")
        val snapshotEvent = SnapshotEvent::class.java.let { objectMapper.readValue(snapshotEventRaw.eventData, it) as SnapshotEvent }
        val snapshotEntity = snapshotRepository.findBySnapshotId(snapshotEvent.snapshotId)
            ?: return Pair(Arena(), "")
        val seats: Array<Array<Seat>> = objectMapper.readValue(snapshotEntity.seatsData)
        return Pair(Arena(seats), snapshotEventRaw.timestamp)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun createSnapshot(arenaId: UUID, eventEntity: ArenaEventEntity) {
        GlobalScope.launch {
            val event = objectMapper.readValue(eventEntity.eventData, SnapshotEvent::class.java)
            val eventsForSnapshotRaw = repository.findByArenaIdAndTimestampLessThan(arenaId, eventEntity.timestamp)
            val eventsForSnapshot = eventsForSnapshotRaw.mapNotNull { entity ->
                val eventClass = when (entity.eventType) { // TODO: to ENUM
                    "ArenaPreparedEvent" -> ArenaPreparedEvent::class.java
                    "SeatReservedEvent" -> SeatReservedEvent::class.java
                    "SeatReleasedEvent" -> SeatReleasedEvent::class.java
                    "SeatReservedCompensationEvent" -> SeatReservedCompensationEvent::class.java
                    "SeatReleasedCompensationEvent" -> SeatReleasedCompensationEvent::class.java
                    else -> null
                }
                eventClass?.let { objectMapper.readValue(entity.eventData, it) as ArenaDomainEvent }
            }
            if (eventsForSnapshot.isNotEmpty()) {
                val arena = Arena()
                eventsForSnapshot.forEach(arena::apply)
                snapshotRepository.save(SnapshotEntity(event.snapshotId, objectMapper.writeValueAsString(arena.seats)))
            }
        }
    }

    override fun loadEvents(arenaId: UUID): List<ArenaDomainEvent> {
        val eventEntities = repository.findByArenaId(arenaId)
        return eventEntities.mapNotNull { entity ->
            val eventClass = when (entity.eventType) { // TODO: to ENUM
                "ArenaPreparedEvent" -> ArenaPreparedEvent::class.java
                "SeatReservedEvent" -> SeatReservedEvent::class.java
                "SeatReleasedEvent" -> SeatReleasedEvent::class.java
                "SeatReservedCompensationEvent" -> SeatReservedCompensationEvent::class.java
                "SeatReleasedCompensationEvent" -> SeatReleasedCompensationEvent::class.java
                else -> null
            }
            eventClass?.let { objectMapper.readValue(entity.eventData, it) as ArenaDomainEvent }
        }
    }

    override fun loadEventsFromTimestamp(arenaId: UUID, timestamp: String): List<ArenaDomainEvent> {
        val eventEntities = repository.findByArenaIdAndTimestampGreaterThan(arenaId, timestamp)
        return eventEntities.mapNotNull { entity ->
            val eventClass = when (entity.eventType) { // TODO: to ENUM
                "ArenaPreparedEvent" -> ArenaPreparedEvent::class.java
                "SeatReservedEvent" -> SeatReservedEvent::class.java
                "SeatReleasedEvent" -> SeatReleasedEvent::class.java
                "SeatReservedCompensationEvent" -> SeatReservedCompensationEvent::class.java
                "SeatReleasedCompensationEvent" -> SeatReleasedCompensationEvent::class.java
                else -> null
            }
            eventClass?.let { objectMapper.readValue(entity.eventData, it) as ArenaDomainEvent }
        }
    }
}
