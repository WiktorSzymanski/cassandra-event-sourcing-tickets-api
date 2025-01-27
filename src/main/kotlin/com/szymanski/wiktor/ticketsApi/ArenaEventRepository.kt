package com.szymanski.wiktor.ticketsApi

import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ArenaEventRepository : CassandraRepository<ArenaEventEntity, UUID> {
    fun findByArenaId(arenaId: UUID): List<ArenaEventEntity>

    @Query("SELECT * FROM arena_event_store WHERE arena_id = ?0 AND eventtype = ?1 ORDER BY timestamp DESC LIMIT 1 ALLOW FILTERING")
    fun findMostRecentByArenaIdAndEventType(arenaId: UUID, eventType: String): ArenaEventEntity?

    @Query("SELECT * FROM arena_event_store WHERE arena_id = ?0 AND timestamp > ?1 ALLOW FILTERING")
    fun findByArenaIdAndTimestampGreaterThan(arenaId: UUID, timestamp: String): List<ArenaEventEntity>

    @Query("SELECT * FROM arena_event_store WHERE arena_id = ?0 AND timestamp < ?1 ALLOW FILTERING")
    fun findByArenaIdAndTimestampLessThan(arenaId: UUID, timestamp: String): List<ArenaEventEntity>
}
