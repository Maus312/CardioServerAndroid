package com.exam.cardioserversimulator.serialcore.data

import kotlinx.serialization.Serializable

@Serializable
data class TimeAndValue(
    val Time: String,
    val Value: Int
)