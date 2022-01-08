package ru.vstu.routes

import io.ktor.application.*

interface RoutesInstaller {
    fun install(application: Application)
}
