@file:OptIn(KtorExperimentalLocationsAPI::class)

package ru.vstu.routes

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import ru.vstu.models.RoomModel
import ru.vstu.repositories.RoomRepository

class RoomRoutesInstaller : RoutesInstaller, StatusPagesConfigurationsInstaller {
    override fun install(application: Application) {
        application.routing {
            getAllRooms()
            addRoom()
        }
    }

    override fun configureStatusPages(configuration: StatusPages.Configuration) {
        configuration.apply {
            exception<WrongRoomReceivedException> { cause ->
                call.respondText(cause.message ?: "", status = HttpStatusCode.BadRequest)
            }
        }
    }
}

@Location("/rooms")
class RoomLocation

private fun Route.getAllRooms() = get<RoomLocation> {
    val roomsRepository: RoomRepository by closestDI().instance()

    val rooms = roomsRepository.findAll()
    call.respond(rooms)
}

private fun Route.addRoom() = post<RoomLocation> {
    val roomsRepository: RoomRepository by closestDI().instance()

    val room = call.receiveOrNull<RoomModel>() ?: throw WrongRoomReceivedException()
    roomsRepository.add(room)
    call.respond(HttpStatusCode.Created)
}

class WrongRoomReceivedException: RuntimeException("Wrong room received.")
