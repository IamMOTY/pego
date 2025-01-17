package com.iammoty.pego.dao

import com.iammoty.pego.model.*
import org.jetbrains.exposed.sql.*

object Users : Table() {
    val id = varchar("user_id", 20)
    val email = varchar("email", 128).uniqueIndex()
    val displayName = varchar("display_name", 256)
    val passwordHash = varchar("password_hash", 64)
    val role = enumeration("role", Role::class)
    val balance = integer("balance")
    override val primaryKey = PrimaryKey(id)
}
