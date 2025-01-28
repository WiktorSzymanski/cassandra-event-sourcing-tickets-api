package com.szymanski.wiktor.ticketsApi

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.*

@Table("snapshot")
class SnapshotEntity (
    @PrimaryKey("snapshot_id") val snapshotId: UUID,
    @Column("seatsdata") val seatsData: String
)