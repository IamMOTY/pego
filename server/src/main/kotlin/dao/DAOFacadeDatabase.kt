package com.iammoty.pego.dao

import com.iammoty.pego.model.*
import org.jetbrains.exposed.sql.*
import org.joda.time.*
import java.io.*

interface DAOFacade : Closeable {
    /**
     * Initializes the Users and Tickets tables.
     */
    fun init()

    /**
     * Creates a Ticket from a specific [user] name, an optional d [date] that would default to the current time.
     */
    fun createTicket(user: String, date: DateTime = DateTime.now()): Int

    /**
     * Deletes a Ticket form its [id].
     */
    fun deleteTicket(id: Int)

    /**
     * Gets the DAO object representation of a ticket based from its [id].
     */
    fun getTicket(id: Int): Ticket

    /**
     * Obtains a list of integral ids of tickets from a specific user identified by its [userId].
     *
     */
    fun userTickets(userId: String): List<Int>

    /**
     * Tries to get an user from its [userId] and optionally is password [hash]
     */
    fun user(userId: String, hash: String? = null): User?

    /**
     * Tries to get an user from its [email].
     *
     * Returns null if no user has this [email] associated.
     */
    fun userByEmail(email: String): User?

    /**
     * Creates a new [user] in the database from its object [User] representation.
     */
    fun createUser(user: User)
}

class DAOFacadeDatabase(val db: Database = Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")) : DAOFacade {
    constructor(dir: File) : this(
        Database.connect(
            "jdbc:h2:file:${dir.canonicalFile.absolutePath}",
            driver = "org.h2.Driver"
        )
    )

    override fun init() {
        db.transaction {
            create(Users, Tickets)
        }
    }

    override fun createTicket(user: String, date: DateTime): Int {
        return db.transaction {
            Tickets.insert {
                it[Tickets.user] = user
                it[Tickets.date] = date
            }.generatedKey ?: throw IllegalStateException("No generated key returned")
        }
    }

    override fun deleteTicket(id: Int) {
        db.transaction {
            Tickets.deleteWhere { Tickets.id.eq(id) }
        }
    }

    override fun getTicket(id: Int) = db.transaction {
        val row = Tickets.select { Tickets.id.eq(id) }.single()
        Ticket(id, row[Tickets.user], row[Tickets.date])
    }

    override fun createUser(user: User) = db.transaction {
        Users.insert {
            it[id] = user.userId
            it[displayName] = user.displayName
            it[email] = user.email
            it[passwordHash] = user.passwordHash
            it[role] = user.role
        }
        Unit
    }

    override fun user(userId: String, hash: String?) = db.transaction {
        Users.select { Users.id.eq(userId) }
            .mapNotNull {
                if (hash == null || it[Users.passwordHash] == hash) {
                    User(
                        userId,
                        it[Users.email],
                        it[Users.displayName],
                        it[Users.passwordHash],
                        it[Users.role],
                        it[Users.balance]
                    )
                } else {
                    null
                }
            }.singleOrNull()
    }

    override fun userByEmail(email: String) = db.transaction {
        Users.select { Users.id.eq(email) }
            .map {
                User(
                    it[Users.id],
                    email,
                    it[Users.displayName],
                    it[Users.passwordHash],
                    it[Users.role],
                    it[Users.balance]
                )
            }
            .singleOrNull()
    }

    override fun userTickets(userId: String) = db.transaction {
        Tickets.slice(Tickets.id).select { Tickets.user.eq(userId) }.orderBy(Tickets.date, false).limit(100)
            .map { it[Tickets.id] }
    }

    override fun close() {
    }
}