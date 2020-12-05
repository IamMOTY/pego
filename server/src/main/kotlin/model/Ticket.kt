package com.iammoty.pego.model

import org.joda.time.DateTime
import java.io.Serializable

class Ticket(val id: Int, val userId: String, val date: DateTime) : Serializable