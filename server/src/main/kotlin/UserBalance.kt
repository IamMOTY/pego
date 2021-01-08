package com.iammoty.pego

import com.iammoty.pego.dao.DAOFacade
import com.iammoty.pego.model.UserResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.routing.get
import io.ktor.sessions.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val ADD_MONEY = 100

fun Route.userBalance(dao: DAOFacade) {

    post<UserBalance> {
        val user = call.sessions.get<PeGoSession>()?.let { dao.user(it.userId)}
        val pageUser = dao.user(it.userId)

        if (user == null || pageUser == null || user.userId != pageUser.userId) {
            println("User not found")
            call.respond(HttpStatusCode.NotFound.description("User ${it.userId} doesn't exist"))
        } else {
            try {
                dao.setNewBalance(pageUser.userId, pageUser.balance + ADD_MONEY)
                val updatedUser = dao.user(pageUser.userId)
                println("new user information $updatedUser")
                call.respond(Json.encodeToString(UserResponse(updatedUser)))
            } catch (e: Throwable) {
                println(e)
                application.log.error("Failed to update balance, ", e)
                call.respond(UserResponse(error = "Failed to update balance"))
            }
        }
    }
}