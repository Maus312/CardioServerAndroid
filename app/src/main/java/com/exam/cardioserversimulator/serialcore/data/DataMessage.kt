package com.exam.cardioserversimulator.serialcore.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Data")
data class DataMessage(
    //override val BLOCK_TYPE: String = "Data",
    val Data: List<DataBlock>
) : Message()

@Serializable
data class DataBlock(
    val CHUNK_NUMBER: Int,
    val BPM: Int,
    val SONG_ID: String,
    val BLOCKS: List<String>
)
