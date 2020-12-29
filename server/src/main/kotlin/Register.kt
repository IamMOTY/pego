package com.iammoty.pego


import com.iammoty.pego.dao.DAOFacade
import com.iammoty.pego.model.Role
import com.iammoty.pego.model.User
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import kotlinx.serialization.json.Json

/**
 * Register routes for user registration in the [Register] route (/register)
 */
fun Route.register(dao: DAOFacade, hashFunction: (String) -> String) {
    /**
     * A POST request to the [Register] route, will try to create a new user.
     *
     * - If the user is already logged, it redirects to the [UserPage] page.
     * - If not specified the userId, password, displayName or email, it will redirect to the [Register] form page.
     * - Then it will validate the specified parameters, redirecting displaying an error to the [Register] page.
     * - On success, it generates a new [User]. But instead of storing the password plain text,
     *   it stores a hash of the password.
     */
    post<Register> {
        // get current from session data if any
        println("Got register request")
        val user = call.sessions.get<PeGoSession>()?.let { dao.user(it.userId) }
        // user already logged in? redirect to user page.
        println(user)
        if (user != null) return@post call.redirect(Login(user.userId))
        println("User is not logged")
        // receive post data
        try {
            val registration = call.receive<User>()

        println("json parse completed successfully")

        println(registration)
        val userId = registration.userId ?: return@post call.redirect(it)
        val password = registration.passwordHash ?: return@post call.redirect(it)
        val displayName = registration.displayName ?: return@post call.redirect(it)
        val email = registration.email ?: return@post call.redirect(it)
        val role = registration.role ?: return@post call.redirect(it)
        // prepare location class for error if any
        val error = Register(userId, displayName, email)

        when {
            password.length < 6 -> call.redirect(error.copy(error = "Password should be at least 6 characters long"))
            userId.length < 4 -> call.redirect(error.copy(error = "Login should be at least 4 characters long"))
            !userNameValid(userId) -> call.redirect(error.copy(error = "Login should be consists of digits, letters, dots or underscores"))
            dao.user(userId) != null -> call.redirect(error.copy(error = "User with the following login is already registered"))
            else -> {
                val hash = hashFunction(password)
                val newUser = User(userId, email, displayName, hash, role, 0)
                try {
                    dao.createUser(newUser)
                } catch (e: Throwable) {
                    when {
                        // NOTE: This is security issue that allows to enumerate/verify registered users. Do not do this in real app :)
                        dao.user(userId) != null -> call.redirect(error.copy(error = "User with the following login is already registered"))
                        dao.userByEmail(email) != null -> call.redirect(error.copy(error = "User with the following email $email is already registered"))
                        else -> {
                            application.log.error("Failed to register user", e)
                            call.redirect(error.copy(error = "Failed to register"))
                        }
                    }
                }

                call.sessions.set(PeGoSession(newUser.userId))
//                call.redirect(UserPage(newUser.userId))
            }
        }
        } catch (e: Exception) {
            println(e)
        }
    }


    get<Register> {
        call.respond(HttpStatusCode.MethodNotAllowed)
    }
}