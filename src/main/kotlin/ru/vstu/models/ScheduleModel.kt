package ru.vstu.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.LocalDate

@Serializable
data class ScheduleModel(
    @Contextual val _id: Id<ScheduleModel> = newId(),
    @Contextual val room: Id<RoomModel>? = null,
    val users: List<@Contextual Id<UserModel>>,
    @Contextual val startDate: LocalDate,
    @Contextual val endDate: LocalDate,
)
