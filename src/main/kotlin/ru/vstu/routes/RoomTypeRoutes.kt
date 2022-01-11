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
import ru.vstu.models.RoomTypeModel
import ru.vstu.repositories.RoomTypeRepository
import ru.vstu.services.RoomTypeNotFoundException

class RoomTypeRoutesInstaller : RoutesInstaller, StatusPagesConfigurationsInstaller {
    override fun install(application: Application) {
        application.routing {
            getAll()
            getRoomTypeById()
            addRoom()
        }
    }

    override fun configureStatusPages(configuration: StatusPages.Configuration) {
        configuration.apply {
            exception<WrongRoomTypeReceivedException> { cause ->
                call.respondText(cause.message ?: "", status = HttpStatusCode.BadRequest)
            }
            exception<RoomTypeNotFoundException> { cause ->
                call.respondText(cause.message ?: "", status = HttpStatusCode.NotFound)
            }
        }
    }
}

@Location("/roomtypes")
class RoomTypeLocation {
    @Location("/{id}")
    data class Id(val parent: RoomTypeLocation, val id: String)
}

private fun Route.getAll() = get<RoomTypeLocation> {
    val roomTypeRepository: RoomTypeRepository by closestDI().instance()

    val roomTypes = roomTypeRepository.findAll()
    call.respond(roomTypes)
}

private fun Route.addRoom() = post<RoomTypeLocation> {
    val roomTypeRepository: RoomTypeRepository by closestDI().instance()

    val roomType = call.receiveOrNull<RoomTypeModel>() ?: throw WrongRoomTypeReceivedException()
    roomTypeRepository.add(roomType)
    call.respond(HttpStatusCode.Created)
}

private fun Route.getRoomTypeById() = get<RoomTypeLocation.Id> {location ->
    val roomTypeRepository: RoomTypeRepository by closestDI().instance()

    val roomType = roomTypeRepository.findById(location.id) ?: throw RoomTypeNotFoundException(location.id)
    call.respond(roomType)
}

class WrongRoomTypeReceivedException: RuntimeException("Wrong room type received.")
