package ru.vstu.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class UserModel(
    @Contextual val _id: Id<UserModel> = newId(),
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    val phone: String,
    val passport: String,
)
