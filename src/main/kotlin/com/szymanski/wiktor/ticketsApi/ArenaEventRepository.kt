package com.szymanski.wiktor.ticketsApi

import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ArenaEventRepository : CassandraRepository<ArenaEventEntity, UUID> {
    fun findByArenaId(arenaId: UUID): List<ArenaEventEntity>
}
