package com.exam.cardioserversimulator.serialcore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Polymorphic

@Serializable
@Polymorphic
sealed class Message {
    //@SerialName("block_type")abstract val BLOCK_TYPE: String
}
