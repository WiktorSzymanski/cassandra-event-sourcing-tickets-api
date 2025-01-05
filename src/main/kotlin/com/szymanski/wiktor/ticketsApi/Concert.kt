package com.szymanski.wiktor.ticketsApi

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.*

@Table("concerts")
class Concert (
    @PrimaryKey val id: String,
    @Column("name") val name: String,
    @Column("arena_id") val arena_id: UUID)

