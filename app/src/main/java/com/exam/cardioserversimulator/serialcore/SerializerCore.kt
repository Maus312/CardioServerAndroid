package com.exam.cardioserversimulator.serialcore

import com.exam.cardioserversimulator.serialcore.data.BiometryMessage
import com.exam.cardioserversimulator.serialcore.data.CommandMessage
import com.exam.cardioserversimulator.serialcore.data.DataMessage
import com.exam.cardioserversimulator.serialcore.data.Message
import com.exam.cardioserversimulator.serialcore.data.SystemMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object SerializerCore {
    val messageSerializersModule = SerializersModule {
        polymorphic(Message::class) {
            subclass(SystemMessage::class, SystemMessage.serializer())
            subclass(BiometryMessage::class, BiometryMessage.serializer())
            subclass(DataMessage::class, DataMessage.serializer())
            subclass(CommandMessage::class, CommandMessage.serializer())
        }
    }

    val json = Json {
        serializersModule = messageSerializersModule
        classDiscriminator = "BLOCK_TYPE"
    }

}

fun CommandMessage.toJson(): String {
    return SerializerCore.json.encodeToString<CommandMessage>(this)
}

fun SystemMessage.toJson(): String {
    return SerializerCore.json.encodeToString<SystemMessage>(this)
}

fun BiometryMessage.toJson(): String {
    return SerializerCore.json.encodeToString<BiometryMessage>(this)
}

fun DataMessage.toJson(): String {
    return SerializerCore.json.encodeToString<DataMessage>(this)
}

fun String.toMessage(): Message{
    return SerializerCore.json.decodeFromString<Message>(this)
}