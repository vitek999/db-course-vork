package ru.vstu.repositories

import org.litote.kmongo.coroutine.CoroutineDatabase
import ru.vstu.models.ScheduleModel

class ScheduleRepository(database: CoroutineDatabase) : CRUDRepository<ScheduleModel>(database.getCollection())