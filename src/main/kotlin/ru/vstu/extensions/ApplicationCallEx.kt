package ru.vstu.extensions

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.respondBadRequest(exception: RuntimeException? = null) {
    respondText(exception?.message ?: "", status = HttpStatusCode.BadRequest)
}

suspend fun ApplicationCall.respondNotFound(exception: RuntimeException? = null) {
    respondText(exception?.message ?: "", status = HttpStatusCode.NotFound)
}