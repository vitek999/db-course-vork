package ru.vstu.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import ru.vstu.models.RoomTypeModel

class RoomTypeRepository(database: CoroutineDatabase): CRUDRepository<RoomTypeModel>(database.getCollection()) {
    suspend fun existsByName(name: String): Boolean = collection.findOne(RoomTypeModel::name eq name) != null
}
