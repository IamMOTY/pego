package com.iammoty.pego

import com.iammoty.pego.dao.*
import com.iammoty.pego.model.*
import com.iammoty.pego.styles
import com.mchange.v2.c3p0.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import org.h2.*
import org.jetbrains.exposed.sql.*
import java.io.*
import java.net.*
import java.text.DateFormat
import java.util.concurrent.*
import javax.crypto.*
import javax.crypto.spec.*


@Location("/")
class Index()


@Location("/user/{userId}")
data class UserPage(val userId: String)

@Location("/register")
data class Register(val userId: String = "", val displayName: String = "", val email: String = "", val error: String = "")

@Location("/login")
data class Login(val userId: String = "", val error: String = "")

@Location("/logout")
class Logout()

@Location("/tickets/{userId}")
data class UserTickets(val userId: String)

@Location("/buy/ticket/{userId}")
data class BuyTicket(val userId: String)

@Location("/balance/{userId}")
data class UserBalance(val userId: String)

/**
 * Represents a session in this site containing the userId.
 */
data class PeGoSession(val userId: String)

/**
 * Hardcoded secret hash key used to hash the passwords, and to authenticate the sessions.
 */
val hashKey = hex("6819b57a326945c1968f45236589")

/**
 * File where the database is going to be stored.
 */
val dir = File("build/db")

/**
 * Pool of JDBC connections used.
 */
val pool = ComboPooledDataSource().apply {
    driverClass = Driver::class.java.name
    jdbcUrl = ""
    user = "admin"
    password = "hujikolp"
}

/**
 * HMac SHA1 key spec for the password hashing.
 */
val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

/**
 * Constructs a facade with the database, connected to the DataSource configured earlier with the [dir]
 * for storing the database.
 */
//val dao: DAOFacade = DAOFacadeCache(DAOFacadeDatabase(Database.connect(pool)), File(dir.parentFile, "ehcache"))
val dao: DAOFacade = DAOFacadeDatabase(Database.connect(pool))
/**
 * Entry Point of the application. This function is referenced in the
 * resources/application.conf file inside the ktor.application.modules.
 *
 */
fun Application.main() {

    dao.init()
    environment.monitor.subscribe(ApplicationStopped) { pool.close() }
    mainWithDependencies(dao)
}

/**
 * This function is called from the entry point and tests to configure an application
 * using the specified [dao] [DAOFacade].
 */
fun Application.mainWithDependencies(dao: DAOFacade) {
    install(DefaultHeaders)
    install(DataConversion)
    install(CallLogging)
    install(ConditionalHeaders)
    install(PartialContent)
    install(Locations)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    install(Sessions) {
        cookie<PeGoSession>("SESSION") {
            transform(SessionTransportTransformerMessageAuthentication(hashKey))
        }
    }

    val hashFunction = { s: String -> hash(s) }

    routing {
        static("/") {
            resources("/")
        }

        styles()
        index(dao)
        userPage(dao)
        userTickets(dao)
        buyTicket(dao)
        userBalance(dao)
        login(dao, hashFunction)
        register(dao, hashFunction)
    }
}

/**
 * Method that hashes a [password] by using the globally defined secret key [hmacKey].
 */
fun hash(password: String): String {
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}

/**
 * Allows to respond with a absolute redirect from a typed [location] instance of a class annotated
 * with [Location] using the Locations feature.
 */
suspend fun ApplicationCall.redirect(location: Any) {
    val host = request.host() ?: "localhost"
    val portSpec = request.port().let { if (it == 80) "" else ":$it" }
    val address = host + portSpec

    respondRedirect("http://$address${application.locations.href(location)}")
}

/**
 * Generates a security code using a [hashFunction], a [date], a [user] and an implicit [HttpHeaders.Referrer]
 * to generate tokens to prevent CSRF attacks.
 */
fun ApplicationCall.securityCode(date: Long, user: User, hashFunction: (String) -> String) =
    hashFunction("$date:${user.userId}:${request.host()}:${refererHost()}")

/**
 * Verifies that a code generated from [securityCode] is valid for a [date] and a [user] and an implicit [HttpHeaders.Referrer].
 * It should match the generated [securityCode] and also not be older than two hours.
 * Used to prevent CSRF attacks.
 */
fun ApplicationCall.verifyCode(date: Long, user: User, code: String, hashFunction: (String) -> String) =
    securityCode(date, user, hashFunction) == code
            && (System.currentTimeMillis() - date).let { it > 0 && it < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS) }

/**
 * Obtains the [refererHost] from the [HttpHeaders.Referrer] header, to check it to prevent CSRF attacks
 * from other domains.
 */
fun ApplicationCall.refererHost() = request.header(HttpHeaders.Referrer)?.let { URI.create(it).host }

/**
 * Pattern to validate an `userId`
 */
private val userIdPattern = "[a-zA-Z0-9_\\.]+".toRegex()

/**
 * Validates that an [userId] (that is also the user name) is a valid identifier.
 * Here we could add additional checks like the length of the user.
 * Or other things like a bad word filter.
 */
internal fun userNameValid(userId: String) = userId.matches(userIdPattern)