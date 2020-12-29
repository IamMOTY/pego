package com.iammoty.pego

import com.iammoty.pego.dao.DAOFacade
import com.iammoty.pego.model.LoginResponse
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

/**
 * Registers the [Login] and [Logout] routes '/login' and '/logout'.
 */
fun Route.login(dao: DAOFacade, hash: (String) -> String) {
    /**
     * A GET request to the [Login], would respond with the login page
     * (unless the user is already logged in, in which case it would redirect to the user's page)
     */
    get<Login> {
        val user = call.sessions.get<PeGoSession>()?.let { dao.user(it.userId) }

        if (user != null) {
            call.respond(LoginResponse(user))
        } else {
            call.respond(HttpStatusCode.Forbidden)
        }
    }

    /**
     * A POST request to the [Login] actually processes the [Parameters] to validate them, if valid it sets the session.
     * It will redirect either to the [Login] page with an error in the case of error,
     * or to the [UserPage] if the login was successful.
     */
    post<Login> {
        val post = call.receive<User>()
        val userId = post.userId ?: return@post call.redirect(it)
        val password = post.passwordHash ?: return@post call.redirect(it)

        val login = when {
            userId.length < 4 -> null
            password.length < 6 -> null
            !userNameValid(userId) -> null
            else -> dao.user(userId, hash(password))
        }

        if (login == null) {
            call.respond(LoginResponse(error = "Invalid username or password"))
        } else {
            call.sessions.set(PeGoSession(login.userId))
            call.respond(LoginResponse(login))
        }
    }

    /**
     * A GET request to the [Logout] page, removes the session and redirects to the [Index] page.
     */
    get<Logout> {
        call.sessions.clear<PeGoSession>()
        call.redirect(Index())
    }
}