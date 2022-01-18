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
import ru.vstu.dtos.rooms.CreateRoomDto
import ru.vstu.extensions.respondBadRequest
import ru.vstu.extensions.respondNotFound
import ru.vstu.services.*

class RoomRoutesInstaller : RoutesInstaller, StatusPagesConfigurationsInstaller {
    override fun install(application: Application) {
        application.routing {
            getAllRooms()
            getRoomById()
            deleteById()
            getRoomsByHotelAndType()
            addRoom()
        }
    }

    override fun configureStatusPages(configuration: StatusPages.Configuration) {
        configuration.apply {
            exception<WrongRoomReceivedException> { cause -> call.respondBadRequest(cause) }
            exception<RoomHasIncorrectType> { cause -> call.respondBadRequest(cause) }
            exception<RoomNotFoundException> { cause -> call.respondNotFound(cause) }
            exception<RoomHasIncorrectHotel> { cause -> call.respondBadRequest(cause) }
            exception<RoomWithNumberAndHotelAlreadyExists> { cause -> call.respondBadRequest(cause) }
            exception<RoomUsedInSchedulesException> { cause -> call.respondBadRequest(cause) }
        }
    }
}

@Location("/rooms")
class RoomLocation {
    @Location("/filter")
    data class Filter(val parent: RoomLocation, val hotelId: String? = null, val typeId: String? = null)
    @Location("/id/{id}")
    data class Id(val parent: RoomLocation, val id: String)
}

private fun Route.getAllRooms() = get<RoomLocation> {
    val roomsService: RoomService by closestDI().instance()

    val rooms = roomsService.getAllRooms()
    call.respond(rooms)
}

private fun Route.getRoomById() = get<RoomLocation.Id> { location ->
    val roomService: RoomService by closestDI().instance()

    val room = roomService.getRoomById(location.id)
    call.respond(room)
}


private fun Route.getRoomsByHotelAndType() = get<RoomLocation.Filter> { location ->
    val roomService: RoomService by closestDI().instance()
    val rooms = roomService.getRoomsByHotelIdAndTypeId(location.hotelId, location.typeId)
    call.respond(rooms)
}

private fun Route.addRoom() = post<RoomLocation> {
    val roomService: RoomService by closestDI().instance()

    val room = call.receiveOrNull<CreateRoomDto>() ?: throw WrongRoomReceivedException()
    roomService.createRoom(room)
    call.respond(HttpStatusCode.Created)
}

private fun Route.deleteById() = delete<RoomLocation.Id> {location ->
    val roomService: RoomService by closestDI().instance()

    roomService.deleteById(location.id)
    call.respond(HttpStatusCode.OK)
}

class WrongRoomReceivedException: RuntimeException("Wrong room received.")
