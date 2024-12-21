package com.szymanski.wiktor.ticketsApi

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.UUID

@Table("arena-event-store")
data class ArenaEventEntity (
    @PrimaryKey val id: UUID,
    val eventType: String,
    val eventData: String
)