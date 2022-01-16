package ru.vstu.services

import ru.vstu.models.RoomTypeModel
import ru.vstu.repositories.RoomTypeRepository

class RoomTypeService(private val roomTypeRepository: RoomTypeRepository) {
    suspend fun create(roomTypeModel: RoomTypeModel) {
        if(roomTypeRepository.existsByName(roomTypeModel.name))
            throw RoomTypeWithNameAlreadyExistsException(roomTypeModel.name)
        roomTypeRepository.add(roomTypeModel)
    }
}

class RoomTypeWithNameAlreadyExistsException(name: String): RuntimeException("Room type with name: $name already exists.")
