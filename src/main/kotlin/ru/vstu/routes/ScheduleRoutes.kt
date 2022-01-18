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
import ru.vstu.dtos.ScheduleDto
import ru.vstu.extensions.respondBadRequest
import ru.vstu.extensions.respondNotFound
import ru.vstu.services.CountOfSleepingPlaceNotEnoughInRoomException
import ru.vstu.services.EndDateOfBookBeforeStartDateException
import ru.vstu.services.ScheduleNotFoundException
import ru.vstu.services.ScheduleService
import java.time.LocalDate

class ScheduleRoutesInstaller : RoutesInstaller, StatusPagesConfigurationsInstaller {
    override fun install(application: Application) {
        application.routing {
            getAvailableRooms()
            bookRoom()
            getAllSchedule()
            getAllScheduleByHotelId()
            deleteById()
        }
    }

    override fun configureStatusPages(configuration: StatusPages.Configuration) {
        configuration.apply {
            exception<WrongScheduleEntityWasReceivedException> { cause -> call.respondBadRequest(cause) }
            exception<CountOfSleepingPlaceNotEnoughInRoomException> { cause -> call.respondBadRequest(cause) }
            exception<EndDateOfBookBeforeStartDateException> { cause -> call.respondBadRequest(cause)}
            exception<ScheduleNotFoundException> { cause -> call.respondNotFound(cause) }
        }
    }
}

@Location("/schedule")
class ScheduleLocation {
    @Location("/book")
    data class Book(val parent: ScheduleLocation)

    @Location("/available")
    data class Available(
        val parent: ScheduleLocation,
        val hotelId: String? = null,
        val typeId: String? = null,
        val startDate: Long,
        val endDate: Long,
    )

    @Location("/history/{hotelId}")
    data class History(
        val parent: ScheduleLocation,
        val hotelId: String
    )

    @Location("/id/{id}")
    data class Id(val parent: ScheduleLocation, val id: String)
}

private fun Route.bookRoom() = post<ScheduleLocation.Book> {
    val scheduleService: ScheduleService by closestDI().instance()

    val schedule = call.receiveOrNull<ScheduleDto>() ?: throw WrongScheduleEntityWasReceivedException()
    scheduleService.bookRoom(schedule)
    call.respond(HttpStatusCode.Created)
}

private fun Route.getAvailableRooms() = get<ScheduleLocation.Available> {location ->
    val scheduleService: ScheduleService by closestDI().instance()

    val startDate = LocalDate.ofEpochDay(location.startDate)
    val endDate = LocalDate.ofEpochDay(location.endDate)
    val rooms = scheduleService.getAvailableRoomsByHotelIdAndTypeId(location.hotelId, location.typeId, startDate, endDate)
    call.respond(rooms)
}

private fun Route.getAllSchedule() = get<ScheduleLocation> {
    val scheduleService: ScheduleService by closestDI().instance()

    val schedules = scheduleService.getAllSchedule()
    call.respond(schedules)
}

private fun Route.getAllScheduleByHotelId() = get<ScheduleLocation.History> { location ->
    val scheduleService: ScheduleService by closestDI().instance()

    val schedules = scheduleService.getSchedulesByHotelId(location.hotelId)
    call.respond(schedules)
}

private fun Route.deleteById() = delete<ScheduleLocation.Id> { location ->
    val scheduleService: ScheduleService by closestDI().instance()

    scheduleService.deleteById(location.id)
    call.respond(HttpStatusCode.OK)
}

class WrongScheduleEntityWasReceivedException : RuntimeException("Wrong schedule entity was received")
