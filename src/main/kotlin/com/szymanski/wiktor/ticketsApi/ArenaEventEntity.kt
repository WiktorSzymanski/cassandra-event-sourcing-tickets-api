package com.szymanski.wiktor.ticketsApi

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.util.UUID

@Table("arena_event_store")
data class ArenaEventEntity (
    @PrimaryKeyColumn(name = "arena_id", type = PrimaryKeyType.PARTITIONED) val arenaId: UUID,
    @PrimaryKeyColumn(name = "timestamp", type = PrimaryKeyType.CLUSTERED) val timestamp: String,
    @Column("eventtype") val eventType: String,
    @Column("eventdata") val eventData: String
)