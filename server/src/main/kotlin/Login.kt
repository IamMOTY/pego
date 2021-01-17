package com.iammoty.pego

import com.iammoty.pego.dao.DAOFacade
import com.iammoty.pego.model.UserResponse
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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception

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
        println("get request recieved ")
        if (user != null) {
            println("User founded $user \n sent message ${UserResponse(user)}")
            call.respond(Json.encodeToString(UserResponse(user)))
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
        println("Post request received")
        val post = Json.decodeFromString<User>(call.receiveText())
        val userId = post.userId
        val password = post.passwordHash
        println("parsing completed successfully -- $post")
        try {
            val login = when {
                userId.length < 4 -> null
                password.length < 6 -> null
                !userNameValid(userId) -> null
                else -> dao.user(userId, hash(password))
            }
            if (login == null) {
                call.respond(Json.encodeToString(UserResponse(error = "Invalid username or password")))
            } else {
                call.sessions.set(PeGoSession(login.userId))
                call.respond(Json.encodeToString(UserResponse(login)))
            }
        } catch (e: Exception) {
            println(e)
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
