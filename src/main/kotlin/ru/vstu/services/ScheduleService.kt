package ru.vstu.services

import org.litote.kmongo.toId
import ru.vstu.dtos.FullScheduleDto
import ru.vstu.dtos.ScheduleDto
import ru.vstu.dtos.rooms.RoomDto
import ru.vstu.models.ScheduleModel
import ru.vstu.repositories.HotelRepository
import ru.vstu.repositories.RoomRepository
import ru.vstu.repositories.ScheduleRepository
import ru.vstu.repositories.UserRepository
import ru.vstu.routes.HotelNotFoundException
import ru.vstu.routes.UserNotFoundException
import java.time.LocalDate

class ScheduleService(
    private val scheduleRepository: ScheduleRepository,
    private val roomService: RoomService,
    private val usersRepository: UserRepository,
    private val hotelRepository: HotelRepository,
    private val roomRepository: RoomRepository,
) {
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

    suspend fun getSchedulesByHotelId(hotelId: String): List<FullScheduleDto> {
        if(!hotelRepository.existsById(hotelId)) throw HotelNotFoundException(hotelId)
        return roomRepository.findAllByHotelIdAndTypeId(hotelId = hotelId, typeId = null).flatMap {
            getSchedulesByRoomId(it._id.toString())
        }
    }

    suspend fun getSchedulesByRoomId(roomId: String): List<FullScheduleDto> {
        if (roomService.isExists(roomId)) throw RoomNotFoundException(roomId)
        val schedules = scheduleRepository.findAllByRoomId(roomId)
        return schedules.map { it.asFullDto() }
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

    private suspend fun ScheduleModel.asFullDto(): FullScheduleDto = FullScheduleDto(
        id = _id.toString(),
        users = users.map { usersRepository.findById(it.toString()) ?: throw UserNotFoundException(it.toString()) },
        startDate = startDate.toEpochDay(),
        endDate = endDate.toEpochDay(),
        roomModel = roomRepository.findById(room.toString()) ?: throw RoomNotFoundException(room.toString())
    )
}

class CountOfSleepingPlaceNotEnoughInRoomException(roomId: String): RuntimeException("Count of sleeping places not enough in room with id: $roomId.")
class EndDateOfBookBeforeStartDateException(startDate: LocalDate, endDate: LocalDate): RuntimeException("End date of book: $endDate before start date: $startDate.")
