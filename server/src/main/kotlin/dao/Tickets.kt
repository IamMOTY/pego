package com.iammoty.pego.dao

import org.jetbrains.exposed.sql.*

object Tickets: Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val user = varchar("user_id", 20).index()
    val date = datetime("date")
}