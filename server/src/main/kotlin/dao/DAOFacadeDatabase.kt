package com.iammoty.pego.dao

import com.iammoty.pego.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.LocalDateTime


class DAOFacadeDatabase(
    val db: Database = Database.connect(
        "jdbc:h2:mem:test",
        driver = "org.h2.Driver",
        user = "root",
        password = ""
    )
) : DAOFacade {
    constructor(dir: File) : this(
        Database.connect(
            "jdbc:h2:file:${dir.canonicalFile.absolutePath}",
            driver = "org.h2.Driver"
        )
    )

    override fun init() {
        transaction(db) {
            SchemaUtils.create(Users, Tickets)
        }
    }

    override fun createTicket(user: String, date: LocalDateTime): Int {
        return transaction(db) {
            Tickets.insert {
                it[Tickets.user] = user
            } get Tickets.id
        }
    }


    override fun deleteTicket(id: Int) {
        transaction(db) {
            Tickets.deleteWhere { Tickets.id.eq(id) }
        }
    }

    override fun getTicket(id: Int) = transaction(db) {
        val row = Tickets.select { Tickets.id.eq(id) }.single()
        Ticket(id, row[Tickets.user], row[Tickets.date].toString())
    }

    override fun createUser(user: User) = transaction(db) {
        Users.insert {
            it[id] = user.userId
            it[email] = user.email
            it[displayName] = user.displayName
            it[passwordHash] = user.passwordHash
            it[role] = user.role
            it[balance] = user.balance
        }
        Unit
    }

    override fun user(userId: String, hash: String?) = transaction(db) {
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

    override fun userByEmail(email: String) = transaction(db) {
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

//    override fun userTickets(userId: String) = transaction(db) {
//        Tickets.slice(Tickets.id).select { Tickets.user.eq(userId) }.orderBy(Tickets.date, false).limit(100)
//            .map { it[Tickets.id] }
//    }

    override fun close() {
    }
}