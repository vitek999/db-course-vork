package ru.vstu.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class RoomTypeModel(
    @Contextual val _id: Id<RoomTypeModel> = newId(),
    val name: String,
    val description: String,
)
