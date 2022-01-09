package ru.vstu.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class RoomModel(
    @Contextual val _id: Id<RoomModel> = newId(),
    val number: String,
    val sleepingPlaces: Int,
    val cost: Int,
    @Contextual val type: Id<RoomTypeModel>? = null
)
