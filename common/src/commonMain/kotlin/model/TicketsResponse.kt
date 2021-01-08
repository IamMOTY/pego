package com.iammoty.pego.model

import kotlinx.serialization.Serializable

@Serializable
data class TicketsResponse(val ticket: List<Int>? = null, val error: String? = null)