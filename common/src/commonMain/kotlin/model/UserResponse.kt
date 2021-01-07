package com.iammoty.pego.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(val user: User? = null, val error: String? = null)
