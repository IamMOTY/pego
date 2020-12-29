package com.iammoty.pego.dao

import com.iammoty.pego.model.*
import java.io.*
import java.time.LocalDateTime

interface DAOFacade : Closeable {
    /**
     * Initializes the Users and Tickets tables.
     */
    fun init()

    /**
     * Creates a Ticket from a specific [user] name, an optional d [date] that would default to the current time.
     */
    fun createTicket(user: String, date: LocalDateTime = LocalDateTime.now()): Int

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
//    fun userTickets(userId: String): List<Int>

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
