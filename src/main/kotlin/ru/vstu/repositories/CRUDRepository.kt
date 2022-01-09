package ru.vstu.repositories

import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.util.KMongoUtil

abstract class CRUDRepository<T : Any>(protected val collection: CoroutineCollection<T>) {
    suspend fun findAll(): List<T> = collection.find().toList()
    suspend fun add(item: T) { collection.insertOne(item) }

    suspend fun findById(id: String): T? = collection.findOneById(ObjectId(id))

    suspend fun deleteById(id: String): T? = collection.findOneAndDelete(KMongoUtil.idFilterQuery(ObjectId(id)))
}
