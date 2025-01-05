package com.szymanski.wiktor.ticketsApi

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.util.UUID

@Table("arena_event_store")
data class ArenaEventEntity (
    @PrimaryKeyColumn(name = "arena_id", type = PrimaryKeyType.PARTITIONED) val arenaId: UUID,
    @PrimaryKeyColumn(name = "time_stamp", type = PrimaryKeyType.CLUSTERED) val timeStamp: String,
    @Column("event_type") val event_type: String,
    @Column("event_data") val event_data: String
)