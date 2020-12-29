import com.iammoty.pego.model.Role
import com.iammoty.pego.model.User
import kotlinext.js.asJsObject
import kotlinx.browser.window
import org.w3c.dom.url.*
import org.w3c.fetch.*
import kotlin.js.*
import kotlinx.serialization.json.*
import kotlinx.serialization.*


// fun index(): IndexResponse =
//    getAndParseResult("/", null, ::parseIndexResponse)


fun register(userId: String, password: String, displayName: String, email: String, role: Role): User =
    postAndParseResult(
        "/register",
        kotlinx.serialization.json.Json.encodeToString(User(userId, email, displayName, password, role)),
        ::parseLoginOrRegisterResponse
    )
// fun pollFromLastTime(lastTime: String = ""): String =
//    getAndParseResult<String>("/poll?lastTime=$lastTime", null, { json ->
//        json.count
//    })

fun checkSession(): User =
    getAndParseResult("/login", null, ::parseLoginOrRegisterResponse)

fun login(userId: String, password: String): User =
    postAndParseResult("/login", URLSearchParams().apply {
        append("userId", userId)
        append("password", password)
    }, ::parseLoginOrRegisterResponse)

// fun postThoughtPrepare(): PostThoughtToken =
//    getAndParseResult("/post-new", null, ::parseNewPostTokenResponse)
//
// fun postThought(replyTo: Int?, text: String, token: PostThoughtToken): Thought =
//    postAndParseResult("/post-new", URLSearchParams().apply {
//        append("text", text)
//        append("date", token.date.toString())
//        append("code", token.code)
//        if (replyTo != null) {
//            append("replyTo", replyTo.toString())
//        }
//    }, ::parsePostThoughtResponse)

fun logoutUser() {
    window.fetch("/logout", object : RequestInit {
        override var method: String? = "POST"
        override var credentials: RequestCredentials? = "same-origin".asDynamic()
    })
}

// fun deleteThought(id: Int, date: Long, code: String) =
//    postAndParseResult("/thought/$id/delete", URLSearchParams().apply {
//        append("date", date.toString())
//        append("code", code)
//    }, { Unit })

//private fun parseIndexResponse(json: dynamic): IndexResponse {
//    val top = json.top as Array<dynamic>
//    val latest = json.latest as Array<dynamic>
//
//    return IndexResponse(top.map(::parseThought), latest.map(::parseThought))
//}
//
//private fun parsePostThoughtResponse(json: dynamic): Thought {
//    return parseThought(json.thought)
//}
//
//private fun parseThought(json: dynamic): Thought {
//    return Thought(json.id, json.userId, json.text, json.date, json.replyTo)
//}
//
//private fun parseNewPostTokenResponse(json: dynamic): PostThoughtToken {
//    return PostThoughtToken(json.user, json.date, json.code)
//}

private fun parseLoginOrRegisterResponse(json: dynamic): User {
    if (json.error != null) {
        throw LoginOrRegisterFailedException(json.error.toString())
    }

    return User(json.user.userId, json.user.email, json.user.displayName, json.user.passwordHash, json.user.role, json.user.balance)
}

class LoginOrRegisterFailedException(message: String) : Throwable(message)

 fun <T> postAndParseResult(url: String, body: dynamic, parse: (dynamic) -> T): T =
    requestAndParseResult("POST", url, body, parse)

 fun <T> getAndParseResult(url: String, body: dynamic, parse: (dynamic) -> T): T =
    requestAndParseResult("GET", url, body, parse)

 fun <T> requestAndParseResult(method: String, url: String, body: dynamic, parse: (dynamic) -> T): T {
    val response = window.fetch(url, object: RequestInit {
        override var method: String? = method
        override var body: dynamic = body
        override var credentials: RequestCredentials? = "same-origin".asDynamic()
        override var headers: dynamic = json("Accept" to "application/json", "Content-Type" to "application/json;charset=UTF-8")
    })
    return parse(response.asJsObject())
}
