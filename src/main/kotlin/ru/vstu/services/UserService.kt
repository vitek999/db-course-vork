package ru.vstu.services

import ru.vstu.models.UserModel
import ru.vstu.repositories.UserRepository

class UserService(private val userRepository: UserRepository) {
    suspend fun create(user: UserModel) {
        if (userRepository.existsByPhone(user.phone)) throw UserWithPhoneAlreadyExistsException(user.phone)
        userRepository.add(user)
    }
}

class UserWithPhoneAlreadyExistsException(phone: String) : RuntimeException("User with phone: $phone already exists.")
