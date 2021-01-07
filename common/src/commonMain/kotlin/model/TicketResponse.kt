package com.iammoty.pego.model

import kotlinx.serialization.Serializable

@Serializable
data class TicketResponse(val ticket: Ticket? = null, val error: String? = null)