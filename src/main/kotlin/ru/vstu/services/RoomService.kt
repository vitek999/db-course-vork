package ru.vstu.services

import org.litote.kmongo.toId
import ru.vstu.dtos.rooms.CreateRoomDto
import ru.vstu.dtos.rooms.RoomDto
import ru.vstu.models.RoomModel
import ru.vstu.repositories.HotelRepository
import ru.vstu.repositories.RoomRepository
import ru.vstu.repositories.RoomTypeRepository
import ru.vstu.repositories.ScheduleRepository
import ru.vstu.routes.HotelNotFoundException

class RoomService(
    private val roomRepository: RoomRepository,
    private val roomTypeRepository: RoomTypeRepository,
    private val hotelRepository: HotelRepository,
    private val scheduleRepository: ScheduleRepository,
) {
    suspend fun getAllRooms(): List<RoomDto> = roomRepository.findAll().map { it.toDto() }

    suspend fun getRoomById(id: String): RoomDto = roomRepository.findById(id)?.toDto() ?: throw RoomNotFoundException(id)

    suspend fun createRoom(dto: CreateRoomDto) {
        val isExists = roomRepository.existsByNumberAndHotel(dto.number, dto.hotel)
        if (isExists) throw RoomWithNumberAndHotelAlreadyExists(dto.number, dto.hotel)
        roomRepository.add(dto.toModel())
    }

    suspend fun deleteById(id: String) {
        if (!roomRepository.existsById(id)) throw RoomNotFoundException(id)
        if(scheduleRepository.findAllByRoomId(id).isNotEmpty()) throw RoomUsedInSchedulesException(id)
        roomRepository.deleteById(id)
    }

    suspend fun isExists(id: String): Boolean = roomTypeRepository.existsById(id)

    suspend fun getRoomsByHotelIdAndTypeId(hotelId: String?, typeId: String?): List<RoomDto> {
        if (hotelId != null && !hotelRepository.existsById(hotelId)) throw HotelNotFoundException(hotelId)
        if (typeId != null && !roomTypeRepository.existsById(typeId)) throw RoomTypeNotFoundException(typeId)
        return roomRepository.findAllByHotelIdAndTypeId(hotelId, typeId).map { it.toDto() }
    }

    private suspend fun RoomModel.toDto(): RoomDto {
        val typeId = type?.toString() ?: throw RoomHasIncorrectType(_id.toString())
        val hotelId = hotel?.toString() ?: throw RoomHasIncorrectHotel(_id.toString())
        val type = roomTypeRepository.findById(typeId) ?: throw RoomTypeNotFoundException(typeId)
        val hotel = hotelRepository.findById(hotelId) ?: throw HotelNotFoundException(hotelId)
        return RoomDto(_id.toString(), number, sleepingPlaces, cost, type, hotel)
    }

    private fun CreateRoomDto.toModel(): RoomModel = RoomModel(
        number = number,
        sleepingPlaces = sleepingPlaces,
        cost = cost,
        type = roomType.toId(),
        hotel = hotel.toId(),
    )
}

class RoomNotFoundException(id: String) : RuntimeException("Room with id: $id not found.")
class RoomTypeNotFoundException(id: String) : RuntimeException("RoomType with id: $id not found.")
class RoomHasIncorrectType(roomId: String) : RuntimeException("Room with id: $roomId has incorrect type.")
class RoomHasIncorrectHotel(roomId: String) : RuntimeException("Room with id: $roomId has incorrect hotel.")
class RoomWithNumberAndHotelAlreadyExists(number: String, hotelId: String) :
    RuntimeException("Room with number: $number already exists in hotel: $hotelId")

class RoomUsedInSchedulesException(id: String): RuntimeException("Room with id: $id used in schedule.")
