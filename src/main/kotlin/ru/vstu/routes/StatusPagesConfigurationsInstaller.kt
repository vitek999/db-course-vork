package ru.vstu.routes

import io.ktor.features.*

interface StatusPagesConfigurationsInstaller {
    fun configureStatusPages(configuration: StatusPages.Configuration)
}