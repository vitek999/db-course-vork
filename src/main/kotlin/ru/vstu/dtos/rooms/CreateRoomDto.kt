package ru.vstu.dtos.rooms

import kotlinx.serialization.Serializable

@Serializable()
data class CreateRoomDto(
    val number: String,
    val sleepingPlaces: Int,
    val cost: Int,
    val roomType: String,
    val hotel: String,
)
