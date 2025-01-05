package com.szymanski.wiktor.ticketsApi

import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface ConcertsRepository : CassandraRepository<Concert, String>
