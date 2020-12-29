package com.iammoty.pego.model


import kotlinx.serialization.Serializable

@Serializable
data class Ticket(val id: Int, val userId: String, val date: String)