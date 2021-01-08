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

const val TICKET_COST = 5

/**
 * Register the [UserTickets] route '/tickets/{user}',
 * with the user ticket list
 */
fun Route.buyTicket(dao:DAOFacade) {

    post<BuyTicket> {
        val user = call.sessions.get<PeGoSession>()?.let { dao.user(it.userId) }
        val pageUser = dao.user(it.userId)
        if (user == null || pageUser == null || user.userId != pageUser.userId ) {
            println("User not found")
            call.respond(HttpStatusCode.NotFound.description("User ${it.userId} doesn't exist"))
        }else  if (user.balance < TICKET_COST){
            call.respond(HttpStatusCode.Forbidden.description("User doesn't have enough money"))
        } else {
            try {
                dao.createTicket(user.userId)
                dao.setNewBalance(user.userId, user.balance - TICKET_COST)
                call.respond(HttpStatusCode.Created)
            } catch (e: Throwable) {
                println(e)
                application.log.error("Failed to create ticket for user $user, ", e)
                call.respond(TicketsResponse(error = "Failed to get users tickets"))
            }


//            call.respond(Json.encodeToString(TicketsResponse()))
        }
    }
}