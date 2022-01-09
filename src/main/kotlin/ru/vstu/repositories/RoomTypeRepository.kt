package ru.vstu.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import ru.vstu.models.RoomTypeModel

class RoomTypeRepository(database: CoroutineDatabase): CRUDRepository<RoomTypeModel>(database.getCollection())