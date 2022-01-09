package ru.vstu

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import ru.vstu.repositories.UserRepository
import ru.vstu.routes.HealthRoutesInstaller
import ru.vstu.routes.UserRoutesInstaller

fun DI.MainBuilder.registerAppBeans() {

    // connect to DB
    val client = KMongo.createClient().coroutine
    val database = client.getDatabase(DATABASE_NAME)

    // repositories
    bindSingleton { UserRepository(database) }

    // routes
    bindSingleton { HealthRoutesInstaller() }
    bindSingleton { UserRoutesInstaller() }
}
