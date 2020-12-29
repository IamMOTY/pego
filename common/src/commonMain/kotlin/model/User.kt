package com.iammoty.pego.model

import kotlinx.serialization.*

@Serializable
data class User(
    val userId: String,
    val email: String,
    val displayName: String,
    val passwordHash: String,
    val role: Role,
    var balance: Int = 0
)