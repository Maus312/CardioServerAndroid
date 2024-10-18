package com.exam.cardioserversimulator.utils

import com.exam.cardioserversimulator.serialcore.data.BiometryMessage
import com.exam.cardioserversimulator.serialcore.data.CommandMessage
import com.exam.cardioserversimulator.serialcore.data.DataMessage
import com.exam.cardioserversimulator.serialcore.data.SystemMessage
import com.exam.cardioserversimulator.serialcore.toJson
import org.java_websocket.WebSocket

fun WebSocket.send(message: SystemMessage){
    this.send(message.toJson())
}
fun WebSocket.send(message: CommandMessage){
    this.send(message.toJson())
}
fun WebSocket.send(message: BiometryMessage){
    this.send(message.toJson())
}
fun WebSocket.send(message: DataMessage){
    this.send(message.toJson())
}