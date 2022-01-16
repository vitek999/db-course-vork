package ru.vstu.repositories

import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.toId
import ru.vstu.models.RoomModel

class RoomRepository(database: CoroutineDatabase): CRUDRepository<RoomModel>(database.getCollection()) {

    suspend fun existsByNumberAndHotel(number: String, hotelId: String): Boolean =
        collection.findOne(and(RoomModel::number eq number, RoomModel::hotel eq hotelId.toId())) != null

    suspend fun findAllByHotelIdAndTypeId(hotelId: String?, typeId: String?): List<RoomModel> {
        val hotelCondition = hotelId?.let { RoomModel::hotel eq hotelId.toId() }
        val typeCondition = typeId?.let { RoomModel::type eq typeId.toId() }
        val resultCondition = when {
            hotelCondition != null && typeCondition != null -> and(typeCondition, hotelCondition)
            hotelCondition != null -> hotelCondition
            typeCondition != null -> typeCondition
            else -> null
        }
        return (resultCondition?.let { collection.find(it) } ?: collection.find()).toList()
    }
}
