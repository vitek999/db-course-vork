package ru.vstu.services

import ru.vstu.models.HotelModel
import ru.vstu.repositories.HotelRepository

class HotelService(private val hotelRepository: HotelRepository){
    suspend fun create(hotelModel: HotelModel) {
        if (hotelRepository.existsByName(hotelModel.name)) throw HotelWithNameAlreadyExists(hotelModel.name)
        hotelRepository.add(hotelModel)
    }
}

class HotelWithNameAlreadyExists(name: String): RuntimeException("Hotel with name: $name already exists.")
