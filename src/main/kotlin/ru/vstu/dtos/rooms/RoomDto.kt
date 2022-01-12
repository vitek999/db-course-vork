package ru.vstu.dtos.rooms

import kotlinx.serialization.Serializable
import ru.vstu.models.HotelModel
import ru.vstu.models.RoomTypeModel

@Serializable
data class RoomDto(
     val id: String,
     val number: String,
     val sleepingPlaces: Int,
     val cost: Int,
     val roomType: RoomTypeModel,
     val hotel: HotelModel,
)