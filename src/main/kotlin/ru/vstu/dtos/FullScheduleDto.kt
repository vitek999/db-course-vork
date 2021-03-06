package ru.vstu.dtos

import kotlinx.serialization.Serializable
import ru.vstu.models.RoomModel
import ru.vstu.models.UserModel

@Serializable
data class FullScheduleDto(
    val id: String? = null,
    val users: List<UserModel>,
    val startDate: String,
    val endDate: String,
    val roomModel: RoomModel,
)
