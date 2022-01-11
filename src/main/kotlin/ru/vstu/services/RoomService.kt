package ru.vstu.services

import ru.vstu.dtos.RoomDto
import ru.vstu.models.RoomModel
import ru.vstu.repositories.RoomRepository
import ru.vstu.repositories.RoomTypeRepository

class RoomService(private val roomRepository: RoomRepository, private val roomTypeRepository: RoomTypeRepository) {
    suspend fun getAllRooms(): List<RoomDto> = roomRepository.findAll().map { it.toDto() }

    suspend fun getRoomById(id: String): RoomDto? = roomRepository.findById(id)?.toDto()

    private suspend fun RoomModel.toDto(): RoomDto {
        val typeId = type?.toString() ?: throw RoomHasIncorrectType(_id.toString())
        val type = roomTypeRepository.findById(typeId) ?: throw RoomTypeNotFoundException(typeId)
        return RoomDto(_id.toString(), number, sleepingPlaces, cost, type)
    }
}

class RoomNotFoundException(id: String): RuntimeException("Room with id: $id not found.")
class RoomTypeNotFoundException(id: String) : RuntimeException("RoomType with id: $id not found.")
class RoomHasIncorrectType(roomId: String): RuntimeException("Room with id: $roomId has incorrect type.")
