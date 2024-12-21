package com.szymanski.wiktor.ticketsApi

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TicketsController {
    @GetMapping("/api")
    fun index(@RequestParam("name") name: String) = "Hello, $name!"
}