package com.szymanski.wiktor.ticketsApi

import org.springframework.data.cassandra.repository.CassandraRepository
import java.util.UUID

interface ArenaEventRepository : CassandraRepository<ArenaEventEntity, UUID>