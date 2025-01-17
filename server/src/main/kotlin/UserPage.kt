package com.iammoty.pego


import com.iammoty.pego.dao.DAOFacade
import com.iammoty.pego.model.UserResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Register the [UserPage] route '/user/{user}',
 * with the user profile.
 */
fun Route.userPage(dao: DAOFacade) {
    /**
     * A GET request will return a page with the profile of a given user from its [UserPage.userId] name.
     * If the user doesn't exists, it will return a 404 page instead.
     */
    get<UserPage> {
        val user = call.sessions.get<PeGoSession>()?.let { dao.user(it.userId) }
        val pageUser = dao.user(it.userId)

        if (user == null || pageUser == null || user.userId != pageUser.userId) {
            println("User not found")
            call.respond(HttpStatusCode.NotFound.description("User ${it.userId} doesn't exist"))
        } else {
//            val tickets = dao.userTickets(it.user).map { dao.getTicket(it) }
//            val etag = (user?.userId ?: "") + "_" + kweets.map { it.text.hashCode() }.hashCode().toString()
            println("Responding user $user")
            call.respond(Json.encodeToString(UserResponse(pageUser)))
        }
    }
}
