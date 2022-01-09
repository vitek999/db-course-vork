import ru.vstu.Versions

plugins {
    application
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

group = "ru.vstu"
version = "0.0.1"
application {
    mainClass.set("ru.vstu.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // ktor
    implementation("io.ktor:ktor-server-core:${Versions.ktor}")
    implementation("io.ktor:ktor-locations:${Versions.ktor}")
    implementation("io.ktor:ktor-server-host-common:${Versions.ktor}")
    implementation("io.ktor:ktor-serialization:${Versions.ktor}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktor}")

    // logs
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")

    // DI (KoDeIn)
    implementation("org.kodein.di:kodein-di:${Versions.kodein}")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:${Versions.kodein}")

    // DB (KMongo)
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:${Versions.kMongo}")
    implementation("org.litote.kmongo:kmongo-id-serialization:${Versions.kMongo}")

    // tests
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
    testImplementation("io.ktor:ktor-server-tests:${Versions.ktor}")
}