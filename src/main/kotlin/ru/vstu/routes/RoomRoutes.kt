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
import ru.vstu.extensions.respondBadRequest
import ru.vstu.extensions.respondNotFound
import ru.vstu.models.RoomModel
import ru.vstu.repositories.RoomRepository
import ru.vstu.services.RoomHasIncorrectType
import ru.vstu.services.RoomNotFoundException
import ru.vstu.services.RoomService

class RoomRoutesInstaller : RoutesInstaller, StatusPagesConfigurationsInstaller {
    override fun install(application: Application) {
        application.routing {
            getAllRooms()
            getRoomById()
            addRoom()
        }
    }

    override fun configureStatusPages(configuration: StatusPages.Configuration) {
        configuration.apply {
            exception<WrongRoomReceivedException> { cause ->
                call.respondBadRequest(cause)
            }
            exception<RoomHasIncorrectType> { cause ->
                call.respondBadRequest(cause)
            }
            exception<RoomNotFoundException> { cause ->
                call.respondNotFound(cause)
            }
        }
    }
}

@Location("/rooms")
class RoomLocation {
    @Location("/{id}")
    data class Id(val parent: RoomLocation, val id: String)
}

private fun Route.getAllRooms() = get<RoomLocation> {
    val roomsService: RoomService by closestDI().instance()

    val rooms = roomsService.getAllRooms()
    call.respond(rooms)
}

private fun Route.getRoomById() = get<RoomLocation.Id> { location ->
    val roomService: RoomService by closestDI().instance()

    val room = roomService.getRoomById(location.id) ?: throw RoomNotFoundException(location.id)
    call.respond(room)
}

private fun Route.addRoom() = post<RoomLocation> {
    val roomsRepository: RoomRepository by closestDI().instance()

    val room = call.receiveOrNull<RoomModel>() ?: throw WrongRoomReceivedException()
    roomsRepository.add(room)
    call.respond(HttpStatusCode.Created)
}

class WrongRoomReceivedException: RuntimeException("Wrong room received.")
