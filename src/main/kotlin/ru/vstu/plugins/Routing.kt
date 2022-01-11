@file:OptIn(KtorExperimentalLocationsAPI::class)

package ru.vstu.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.features.*
import io.ktor.application.*
import io.ktor.response.*
import org.kodein.di.allInstances
import org.kodein.di.ktor.closestDI
import ru.vstu.routes.StatusPagesConfigurationsInstaller

fun Application.configureRouting() {
    install(Locations) {
    }


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get<MyLocation> {
            call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
        }
        // Register nested routes
        get<Type.Edit> {
            call.respondText("Inside $it")
        }
        get<Type.List> {
            call.respondText("Inside $it")
        }
        install(StatusPages) {
            val statusPagesInstallers by closestDI().allInstances<StatusPagesConfigurationsInstaller>()

            statusPagesInstallers.forEach { it.configureStatusPages(this) }

            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }
            exception<IllegalArgumentException> { cause ->
                call.respondText(cause.message ?: "", status = HttpStatusCode.BadRequest)
            }
        }
    }
}

@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")
@Location("/type/{name}")
data class Type(val name: String) {
    @Location("/edit")
    data class Edit(val type: Type)

    @Location("/list/{page}")
    data class List(val type: Type, val page: Int)
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
