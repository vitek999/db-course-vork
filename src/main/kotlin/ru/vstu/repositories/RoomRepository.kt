package ru.vstu.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import ru.vstu.models.RoomModel

class RoomRepository(database: CoroutineDatabase): CRUDRepository<RoomModel>(database.getCollection())