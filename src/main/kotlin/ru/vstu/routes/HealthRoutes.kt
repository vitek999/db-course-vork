@file:OptIn(KtorExperimentalLocationsAPI::class)

package ru.vstu.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

class HealthRoutesInstaller : RoutesInstaller {
    override fun install(application: Application) {
        application.routing {
            health()
        }
    }
}

@Location("/health")
class HealthLocation

private fun Route.health() = get<HealthLocation> {
    call.respondText("ok")
}
