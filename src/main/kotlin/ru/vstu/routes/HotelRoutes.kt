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
import ru.vstu.models.HotelModel
import ru.vstu.repositories.HotelRepository
import ru.vstu.services.HotelService
import ru.vstu.services.HotelWithNameAlreadyExists

class HotelRoutesInstaller : RoutesInstaller, StatusPagesConfigurationsInstaller {
    override fun install(application: Application) {
        application.routing {
            getAll()
            getById()
            addHotel()
            deleteById()
        }
    }

    override fun configureStatusPages(configuration: StatusPages.Configuration) {
        configuration.apply {
            exception<WrongHotelReceivedException> { cause ->
                call.respondText(cause.message ?: "", status = HttpStatusCode.BadRequest)
            }
            exception<HotelNotFoundException> { cause ->
                call.respondText(cause.message ?: "", status = HttpStatusCode.NotFound)
            }
            exception<HotelWithNameAlreadyExists> { cause -> call.respondBadRequest(cause) }
        }
    }
}

@Location("/hotels")
class HotelLocation {
    @Location("/{id}")
    data class Id(val parent: HotelLocation, val id: String)
}

private fun Route.getAll() = get<HotelLocation> {
    val hotelsRepository: HotelRepository by closestDI().instance()

    call.respond(hotelsRepository.findAll())
}

private fun Route.addHotel() = post<HotelLocation> {
    val hotelService: HotelService by closestDI().instance()

    val hotel = call.receiveOrNull<HotelModel>() ?: throw WrongHotelReceivedException()
    hotelService.create(hotel)
    call.respond(HttpStatusCode.Created)
}

private fun Route.getById() = get<HotelLocation.Id> { location ->
    val hotelsRepository: HotelRepository by closestDI().instance()

    val hotel = hotelsRepository.findById(location.id) ?: throw HotelNotFoundException(location.id)
    call.respond(hotel)
}

private fun Route.deleteById() = delete<HotelLocation.Id> {location ->
    val hotelsRepository: HotelRepository by closestDI().instance()

    hotelsRepository.deleteById(location.id)
    call.respond(HttpStatusCode.OK)
}

class WrongHotelReceivedException : RuntimeException("Wrong hotel received.")
class HotelNotFoundException(id: String): RuntimeException("Hotel with id: $id not found.")
