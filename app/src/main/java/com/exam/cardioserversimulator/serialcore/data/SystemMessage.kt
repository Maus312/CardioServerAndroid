package com.exam.cardioserversimulator.serialcore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("System")
data class SystemMessage(
    //override val BLOCK_TYPE: String = "System",
    val TYPE_ID: Int? = null,
    val sessionId: String? = null,
    val ERROR_REASON: String? = null,
    val STATUS: String? = null,

) : Message()
