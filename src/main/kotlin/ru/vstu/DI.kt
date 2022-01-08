package ru.vstu

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import ru.vstu.routes.HealthRoutesInstaller

fun DI.MainBuilder.registerAppBeans() {
    // routes
    bindSingleton { HealthRoutesInstaller() }
}
