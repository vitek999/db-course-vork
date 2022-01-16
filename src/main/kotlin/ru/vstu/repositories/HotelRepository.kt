package ru.vstu.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import ru.vstu.models.HotelModel

class HotelRepository(database: CoroutineDatabase) : CRUDRepository<HotelModel>(database.getCollection()) {
    suspend fun existsByName(name: String): Boolean = collection.findOne(HotelModel::name eq name) != null
}