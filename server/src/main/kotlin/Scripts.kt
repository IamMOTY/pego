package com.iammoty.pego

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*


@Location("/scripts/client.js")
class ClientJs()



fun Route.scripts() {

    get<MainCss> {
        call.respond(call.resolveResource("client.js")!!)
    }
}
