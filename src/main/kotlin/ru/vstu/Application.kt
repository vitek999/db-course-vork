package ru.vstu

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.kodein.di.allInstances
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import ru.vstu.plugins.*
import ru.vstu.routes.RoutesInstaller

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        di { registerAppBeans() }
        configureCORS()
        configureRouting()
        configureSerialization()

        val routesInstallers by closestDI().allInstances<RoutesInstaller>()
        routesInstallers.forEach { it.install(this) }
    }.start(wait = true)
}
