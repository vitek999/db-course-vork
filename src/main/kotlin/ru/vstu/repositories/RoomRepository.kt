package ru.vstu.repositories

import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.toId
import ru.vstu.models.RoomModel

class RoomRepository(database: CoroutineDatabase): CRUDRepository<RoomModel>(database.getCollection()) {

    suspend fun existsByNumberAndHotel(number: String, hotelId: String): Boolean =
        collection.findOne(and(RoomModel::number eq number, RoomModel::hotel eq hotelId.toId())) != null
}
