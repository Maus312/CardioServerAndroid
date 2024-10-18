package com.exam.cardioserversimulator.serialcore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Command")
data class CommandMessage(
    //override val BLOCK_TYPE: String = "Command",
    val STATUS: String?,
    val ACTIVITY_ID: Int?,
    val COMMAND_ID: Int?,
    val ADDITIONAL_DATA: String?
) : Message()
