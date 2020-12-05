package com.iammoty.pego.model

import java.io.Serializable

data class User(
    val userId: String,
    val email: String,
    val displayName: String,
    val passwordHash: String,
    val role: Role,
    val balance: Int
) : Serializable