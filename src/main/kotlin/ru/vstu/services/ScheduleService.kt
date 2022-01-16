package ru.vstu.services

import org.litote.kmongo.toId
import ru.vstu.dtos.ScheduleDto
import ru.vstu.dtos.rooms.RoomDto
import ru.vstu.models.ScheduleModel
import ru.vstu.repositories.ScheduleRepository
import java.time.LocalDate

class ScheduleService(private val scheduleRepository: ScheduleRepository, private val roomService: RoomService) {
    suspend fun getAvailableRoomsByHotelIdAndTypeId(
        hotelId: String? = null,
        typeId: String? = null,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<RoomDto> {
        if (endDate.isBefore(startDate)) throw EndDateOfBookBeforeStartDateException(startDate, endDate)
        val allRoomsByFilter = roomService.getRoomsByHotelIdAndTypeId(hotelId, typeId)
        val bookedRoomsIds = scheduleRepository.findAll().filter {
            (startDate.isAfter(it.startDate) && startDate.isBefore(it.endDate))
                    || (endDate.isAfter(it.startDate) && endDate.isBefore(it.endDate))
                    || (startDate.isBefore(it.startDate) && ((endDate.isBefore(it.endDate) && endDate.isAfter(it.startDate)) || endDate.isAfter(it.endDate)))
                    || (endDate.isAfter(it.endDate) && ((startDate.isBefore(it.endDate) && startDate.isAfter(it.startDate)) || startDate.isBefore(it.startDate)))
                    || startDate == it.endDate || startDate == it.startDate || endDate == it.endDate || endDate == it.startDate
        }.map { it.room.toString() }
        return allRoomsByFilter.filterNot { it.id in bookedRoomsIds }
    }

    suspend fun getAllSchedule(): List<ScheduleDto> = scheduleRepository.findAll().map { it.asDto() }

    suspend fun bookRoom(scheduleDto: ScheduleDto) {
        val startDate = LocalDate.ofEpochDay(scheduleDto.startDate)
        val endDate = LocalDate.ofEpochDay(scheduleDto.endDate)
        if (endDate.isBefore(startDate)) throw EndDateOfBookBeforeStartDateException(startDate, endDate)
        val room = roomService.getRoomById(scheduleDto.roomId)
        if (room.sleepingPlaces < scheduleDto.users.size) throw CountOfSleepingPlaceNotEnoughInRoomException(scheduleDto.roomId)
        scheduleRepository.add(scheduleDto.asModel())
    }

    private fun ScheduleModel.asDto(): ScheduleDto = ScheduleDto(
        _id.toString(),
        room.toString(),
        users.map { it.toString() },
        startDate.toEpochDay(),
        endDate.toEpochDay(),
    )

    private fun ScheduleDto.asModel(): ScheduleModel = ScheduleModel(
        room = roomId.toId(),
        users = users.map { it.toId() },
        startDate = LocalDate.ofEpochDay(startDate),
        endDate = LocalDate.ofEpochDay(endDate)
    )
}

class CountOfSleepingPlaceNotEnoughInRoomException(roomId: String): RuntimeException("Count of sleeping places not enough in room with id: $roomId.")
class EndDateOfBookBeforeStartDateException(startDate: LocalDate, endDate: LocalDate): RuntimeException("End date of book: $endDate before start date: $startDate.")
