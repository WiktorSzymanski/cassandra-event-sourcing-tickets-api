package com.szymanski.wiktor.ticketsApi

import java.io.File
import java.io.IOException
import java.util.UUID
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class FileEventStore(private val directory: String) : EventStore {
    val objectMapper = jacksonObjectMapper()

    init {
        val dir = File(directory)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    override fun saveEvent(arenaId: UUID, event: ArenaDomainEvent) {
        val file = File("$directory/${arenaId}.txt")
        if (!file.exists()) { file.createNewFile() }
        try {
            file.appendText("${mapEventType(event)} | ${serializeEvent(event)}\n")
            println("Event saved for arena: $arenaId")
        } catch (e: IOException) {
            println("Error saving event: ${e.message}")
        }
    }

    override fun loadEvents(arenaId: UUID): List<ArenaDomainEvent> {
        val file = File("$directory/$arenaId.txt")
        if (!file.exists()) { file.createNewFile() }
        val events = mutableListOf<ArenaDomainEvent>()

        file.useLines { lines ->
            lines.forEach {
                events.add(createEvent(it))
            }
        }

        return events.toList()
    }

    fun createEvent(string: String): ArenaDomainEvent {
        val (eventType, data) = string.split("|").map { it.trim() }
        return when (eventType) {
            "ArenaPreparedEvent" -> deserializeEvent<ArenaPreparedEvent>(data)
            "SeatReservedEvent" -> deserializeEvent<SeatReservedEvent>(data)
            "SeatReleasedEvent" -> deserializeEvent<SeatReleasedEvent>(data)
            else -> throw IllegalArgumentException("Unknown event type: $eventType")
        }
    }

    fun serializeEvent(event: ArenaDomainEvent): String {
        return objectMapper.writeValueAsString(event)
    }

    inline fun <reified T : ArenaDomainEvent> deserializeEvent(json: String): T {
        return objectMapper.readValue(json)
    }

    private fun mapEventType(event : ArenaDomainEvent): String {
        return when (event) {
            is ArenaPreparedEvent -> "ArenaPreparedEvent"
            is SeatReservedEvent -> "SeatReservedEvent"
            is SeatReleasedEvent -> "SeatReleasedEvent"
            else -> "UnknownEvent"
        }
    }
}

//fun main() {
//
//    val arenaId = UUID.randomUUID()
//
//    val repo = ArenaRepo(FileEventStore("./.event-store"))
//
//    repo.apply(arenaId, ArenaPreparedEvent(UUID.randomUUID(), 10, 10))
//    repo.apply(arenaId, SeatReservedEvent(UUID.randomUUID(), row = 5, seat = 9, username = "John Doe"))
//    repo.apply(arenaId, SeatReleasedEvent(UUID.randomUUID(), row = 5, seat = 9))
//    repo.apply(arenaId, SeatReservedEvent(UUID.randomUUID(), row = 5, seat = 5, username = "Ben Dover"))
//
//
//    val arena = Arena()
//    arena.apply(ArenaPreparedEvent(UUID.randomUUID(), 10, 10))
//    arena.apply(SeatReservedEvent(UUID.randomUUID(), row = 5, seat = 9, username = "John Doe"))
//    arena.apply(SeatReleasedEvent(UUID.randomUUID(), row = 5, seat = 9))
//    arena.apply(SeatReservedEvent(UUID.randomUUID(), row = 5, seat = 5, username = "Ben Dover"))
//
//    val loaded = repo.load(arenaId)
//    if (loaded.equals(arena))
//        println("Loaded and deserialized data are equal")
//    else println("Loaded and deserialized data are not equal")
//}
