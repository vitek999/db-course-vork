package ru.vstu.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import ru.vstu.models.UserModel

class UserRepository(database: CoroutineDatabase) : CRUDRepository<UserModel>(database.getCollection()) {
    suspend fun findByPhoneOrNull(phone: String): UserModel? = collection.findOne(UserModel::phone eq phone)
    suspend fun deleteByPhone(phone: String) {
        collection.deleteOne(UserModel::phone eq phone)
    }
}
