@file:OptIn(KtorExperimentalLocationsAPI::class)

package ru.vstu.routes

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.litote.kmongo.MongoOperator
import ru.vstu.extensions.respondBadRequest
import ru.vstu.models.UserModel
import ru.vstu.repositories.UserRepository
import ru.vstu.services.UserService
import ru.vstu.services.UserWithPhoneAlreadyExistsException

class UserRoutesInstaller : RoutesInstaller, StatusPagesConfigurationsInstaller {
    override fun install(application: Application) {
        application.routing {
            getAllUsers()
            getUserByPhone()
            createUser()
            deleteUserByPhone()
        }
    }

    override fun configureStatusPages(configuration: StatusPages.Configuration) {
        configuration.apply {
            exception<WrongUserReceivedException> { cause ->
                call.respondText(cause.message ?: "", status = HttpStatusCode.BadRequest)
            }
            exception<UserNotFoundException> { cause ->
                call.respondText(cause.message ?: "", status = HttpStatusCode.NotFound)
            }
            exception<UserWithPhoneAlreadyExistsException> { cause -> call.respondBadRequest(cause) }
        }
    }
}

@Location("/users")
class UsersLocation {
    @Location("/{phone}")
    data class Phone(val parent: UsersLocation, val phone: String)
}

private fun Route.getAllUsers() = get<UsersLocation> {
    val usersRepository: UserRepository by closestDI().instance()

    val users = usersRepository.findAll()
    call.respond(users)
}

private fun Route.createUser() = post<UsersLocation> {
    val userService: UserService by closestDI().instance()

    val user = call.receiveOrNull<UserModel>() ?: throw WrongUserReceivedException()
    userService.create(user)
    call.respond(HttpStatusCode.Created)
}

private fun Route.deleteUserByPhone() = delete<UsersLocation.Phone> { location ->
    val usersRepository: UserRepository by closestDI().instance()

    usersRepository.deleteByPhone(location.phone)
    call.respond(HttpStatusCode.OK)
}

private fun Route.getUserByPhone() = get<UsersLocation.Phone> { location ->
    val usersRepository: UserRepository by closestDI().instance()

    val user = usersRepository.findByPhoneOrNull(location.phone) ?: throw UserNotFoundException(location.phone)
    call.respond(user)
}

class WrongUserReceivedException: RuntimeException("Wrong user received.")
class UserNotFoundException(phone: String): RuntimeException("User with phone: $phone not found.")
