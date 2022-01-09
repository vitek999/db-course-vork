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

class RoomTypeRoutesInstaller : RoutesInstaller, StatusPagesConfigurationsInstaller {
    override fun install(application: Application) {
        application.routing {
            getAll()
            addRoom()
        }
    }

    override fun configureStatusPages(configuration: StatusPages.Configuration) {
        configuration.apply {
            exception<WrongRoomTypeReceivedException> { cause ->
                call.respondText(cause.message ?: "", status = HttpStatusCode.BadRequest)
            }
        }
    }
}

@Location("/roomtypes")
class RoomTypeLocation

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

class WrongRoomTypeReceivedException: RuntimeException("Wrong room type received.")
