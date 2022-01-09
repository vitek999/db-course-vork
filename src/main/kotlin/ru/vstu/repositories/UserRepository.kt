package ru.vstu.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import ru.vstu.models.UserModel

class UserRepository(database: CoroutineDatabase) {
    private val collection = database.getCollection<UserModel>()

    suspend fun findAll(): List<UserModel> = collection.find().toList()

    suspend fun findByPhoneOrNull(phone: String): UserModel? = collection.findOne(UserModel::phone eq phone)

    suspend fun add(user: UserModel) {
        collection.insertOne(user)
    }

    suspend fun deleteByPhone(phone: String) {
        collection.deleteOne(UserModel::phone eq phone)
    }
}
