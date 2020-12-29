package com.iammoty.pego.model

interface RpcData

data class LoginResponse(val user: User? = null, val error: String? = null) : RpcData
