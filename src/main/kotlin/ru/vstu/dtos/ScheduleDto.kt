package ru.vstu.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDto(
    val id: String? = null,
    val roomId: String,
    val users: List<String>,
    val startDate: Long,
    val endDate: Long,
)
