import com.iammoty.pego.model.*
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.*
import kotlin.js.*
import kotlinx.serialization.json.*
import kotlinx.serialization.*


// fun index(): IndexResponse =
//    getAndParseResult("/", null, ::parseIndexResponse)


suspend fun register(userId: String, password: String, displayName: String, email: String, role: Role): User =
    postAndParseResult(
        "/register",
        kotlinx.serialization.json.Json.encodeToString(User(userId, email, displayName, password, role)),
        ::parseLoginOrRegisterResponse
    )
// fun pollFromLastTime(lastTime: String = ""): String =
//    getAndParseResult<String>("/poll?lastTime=$lastTime", null, { json ->
//        json.count
//    })

suspend fun checkSession(): User =
    getAndParseResult("/login", null, ::parseLoginOrRegisterResponse)

suspend fun login(userId: String, password: String): User =
    postAndParseResult(
        "/login",
        kotlinx.serialization.json.Json.encodeToString(User(userId = userId, passwordHash = password)),
        ::parseLoginOrRegisterResponse
    )

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
        override var method: String? = "GET"
        override var credentials: RequestCredentials? = "same-origin".asDynamic()
    })
}

suspend fun updateBalance(userId: String): User =
    postAndParseResult(
        "/balance/$userId",
        null,
        ::parseLoginOrRegisterResponse
    )

suspend fun getUserTickets(userId: String): List<Int>? =
    getAndParseResult(
        "/tickets/$userId",
        null,
        ::parseTicketsResponse
    )

suspend fun buyTicket(userId: String) {
    window.fetch("/buy/ticket/$userId", object : RequestInit {
        override var method: String? = "POST"
        override var credentials: RequestCredentials? = "same-origin".asDynamic()
    })
}

private fun parseLoginOrRegisterResponse(json: dynamic): User {
    val dec = kotlinx.serialization.json.Json.decodeFromDynamic<UserResponse>(json)
    println(dec)
    if (dec.error != null) {
        println("error isn't empty ${dec.error}")
        throw LoginOrRegisterFailedException(json.error.toString())
    }
    println("user parsed successfully")
    return dec.user!!
}

private fun parseTicketsResponse(json: dynamic): List<Int>? {
    val dec = kotlinx.serialization.json.Json.decodeFromDynamic<TicketsResponse>(json)
    println(dec)
    if (dec.error != null) {
        println("error isn't empty ${dec.error}")
        throw GetTicketsException(dec.error.toString())
    }
    println("tickets list parsed successfully")

    return dec.ticket
}

class LoginOrRegisterFailedException(message: String) : Throwable(message)
class GetTicketsException(message: String) : Throwable(message)

 suspend fun <T> postAndParseResult(url: String, body: dynamic, parse: (dynamic) -> T): T =
    requestAndParseResult("POST", url, body, parse)

 suspend fun <T> getAndParseResult(url: String, body: dynamic, parse: (dynamic) -> T): T =
    requestAndParseResult("GET", url, body, parse)

 suspend fun <T> requestAndParseResult(method: String, url: String, body: dynamic, parse: (dynamic) -> T): T {
    val response = Promise.resolve(window.fetch(url, object: RequestInit {
        override var method: String? = method
        override var body: dynamic = body
        override var credentials: RequestCredentials? = "same-origin".asDynamic()
        override var headers: dynamic = json("Accept" to "application/json")
    }).then {response->
        response.json()
    }.then {
        it
    }).await()
    return parse(response)
}
