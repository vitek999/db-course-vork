package ru.vstu.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import ru.vstu.models.HotelModel

class HotelRepository(database: CoroutineDatabase) : CRUDRepository<HotelModel>(database.getCollection())