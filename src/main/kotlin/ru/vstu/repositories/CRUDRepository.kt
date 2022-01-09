package ru.vstu.repositories

import org.litote.kmongo.coroutine.CoroutineCollection

abstract class CRUDRepository<T : Any>(protected val collection: CoroutineCollection<T>) {
    suspend fun findAll(): List<T> = collection.find().toList()
    suspend fun add(item: T) { collection.insertOne(item) }
}
