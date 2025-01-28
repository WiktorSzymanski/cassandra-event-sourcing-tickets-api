package com.szymanski.wiktor.ticketsApi

import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SnapshotRepository : CassandraRepository<SnapshotEntity, UUID> {
    fun findBySnapshotId(arenaId: UUID): SnapshotEntity?
}
