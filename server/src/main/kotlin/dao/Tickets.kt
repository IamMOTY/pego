package com.iammoty.pego.dao

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.jodatime.datetime

object Tickets: Table() {
    val id = integer("ticket_id").autoIncrement()
    val user = varchar("user_id", 20).index()
    val date = datetime("date")
    override val primaryKey = PrimaryKey(id)
}