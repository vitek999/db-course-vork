package ru.vstu.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.toId
import ru.vstu.models.ScheduleModel

class ScheduleRepository(database: CoroutineDatabase) : CRUDRepository<ScheduleModel>(database.getCollection()) {
    suspend fun findAllByRoomId(roomId: String): List<ScheduleModel> =
        collection.find(ScheduleModel::room eq roomId.toId()).toList()
}