package com.szymanski.wiktor.ticketsApi

import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class ConcertService(private val repository: ConcertsRepository) {

    fun createConcert(name: String): Concert {
        return repository.save(Concert(UUID.randomUUID().toString(), name, UUID.randomUUID()))
    }

    fun getConcerts(): List<Concert> {
        return repository.findAll()
    }

    fun getConcertById(id: UUID): Concert {
        return repository.findById(id.toString()).getOrElse { throw NoSuchConcertException(id) }
    }
}