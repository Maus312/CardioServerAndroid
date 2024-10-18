package com.exam.cardioserversimulator.serialcore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Biometry")
data class BiometryMessage(
    //override val BLOCK_TYPE: String = "Pulse",
    val OXI: List<TimeAndValue>? = null,
    val ECG: List<TimeAndValue>? = null,
    val BPM: List<TimeAndValue>? = null,

    val TYPE_ID: Int
) : Message()
