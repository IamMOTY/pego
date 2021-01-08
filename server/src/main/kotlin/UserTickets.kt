package com.iammoty.pego

import com.iammoty.pego.dao.DAOFacade
import com.iammoty.pego.model.TicketsResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Register the [UserTickets] route '/tickets/{user}',
 * with the user ticket list
 */
fun Route.userTickets(dao:DAOFacade) {

    get<UserTickets> {
        val user = call.sessions.get<PeGoSession>()?.let { dao.user(it.userId) }
        val pageUser = dao.user(it.userId)
        if (user == null || pageUser == null || user.userId != pageUser.userId) {
            println("User not found")
            call.respond(HttpStatusCode.NotFound.description("User ${it.userId} doesn't exist"))
        } else {
            try {
                val list = dao.userTickets(user.userId)
                println("Responding ticket list for user $user \n ${list}")
                call.respond(Json.encodeToString(TicketsResponse(list)))
            } catch (e: Throwable) {
                println(e)
                application.log.error("Failed to get users tickets, ", e)
                call.respond(TicketsResponse(error = "Failed to get users tickets"))
            }


//            call.respond(Json.encodeToString(TicketsResponse()))
        }
    }
}