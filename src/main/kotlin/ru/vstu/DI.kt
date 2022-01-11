package ru.vstu

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import ru.vstu.repositories.HotelRepository
import ru.vstu.repositories.RoomRepository
import ru.vstu.repositories.RoomTypeRepository
import ru.vstu.repositories.UserRepository
import ru.vstu.routes.*
import ru.vstu.services.RoomService

fun DI.MainBuilder.registerAppBeans() {

    // connect to DB
    val client = KMongo.createClient().coroutine
    val database = client.getDatabase(DATABASE_NAME)

    // repositories
    bindSingleton { UserRepository(database) }
    bindSingleton { HotelRepository(database) }
    bindSingleton { RoomTypeRepository(database) }
    bindSingleton { RoomRepository(database) }

    // services
    bindSingleton { RoomService(instance(), instance()) }

    // routes
    bindSingleton { HealthRoutesInstaller() }
    bindSingleton { UserRoutesInstaller() }
    bindSingleton { HotelRoutesInstaller() }
    bindSingleton { RoomRoutesInstaller() }
    bindSingleton { RoomTypeRoutesInstaller() }
}
