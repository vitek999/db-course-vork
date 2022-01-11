package ru.vstu.dtos

import kotlinx.serialization.Serializable
import ru.vstu.models.RoomTypeModel

@Serializable
data class RoomDto(
     val id: String,
     val number: String,
     val sleepingPlaces: Int,
     val cost: Int,
     val roomType: RoomTypeModel,
)