package com.iammoty.pego

import com.iammoty.pego.dao.*
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.ContentType.Text.Html
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.html.*


fun Route.index(doa: DAOFacade) {

    get<Index> {
        // Tries to get the user from the session (null if failure)
        val user = call.sessions.get<PeGoSession>()?.let { dao.user(it.userId) }



        call.respondHtml {
            head {
                meta("viewport", "width=device-width, initial-scale=1") {}
                link("https://fonts.googleapis.com/css?family=Roboto:300,400,500", rel = "stylesheet")
                link("https://fonts.googleapis.com/icon?family=Material+Icons", rel = "stylesheet")
            }
            body {
                noScript {
                    +"You need to install JavaScript to run this app"
                }
                div {
                    id = "root"
                }
                script {
                    src = "client.js"
                }
            }
        }

    }
}