package com.exam.cardioserversimulator

import android.content.Context
import android.util.Log
import com.exam.cardioserversimulator.serialcore.data.CommandMessage
import com.exam.cardioserversimulator.serialcore.data.DataBlock
import com.exam.cardioserversimulator.serialcore.data.DataMessage
import com.exam.cardioserversimulator.serialcore.data.DeviceStatuses
import com.exam.cardioserversimulator.serialcore.data.BiometryMessage
import com.exam.cardioserversimulator.serialcore.data.SystemMessage
import com.exam.cardioserversimulator.serialcore.toJson
import com.exam.cardioserversimulator.serialcore.toMessage
import com.exam.cardioserversimulator.utils.send
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class ServerSocketThread (private val context: Context) : Thread() {
    private lateinit var server: WebSocketServer
    private val clients = mutableListOf<ClientInfo>()


    data class ClientInfo(
        val webSocket: WebSocket,
        var sessionId: String? = null,
        var typeId: Int? = null
    )

    override fun run() {
        val port = 8080 // Задайте желаемый порт

        server = object : WebSocketServer(InetSocketAddress(port)) {
            override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
                val logMessage = "Соединение открыто: ${conn?.remoteSocketAddress}"
                Log.d("WebSocket", logMessage)
                LogRepository.log(context, logMessage)

                conn?.let {
                    val clientInfo = ClientInfo(webSocket = it)
                    synchronized(clients) {
                        clients.add(clientInfo)
                    }
                    vibrateDevice(context)
                    showToast(context,"Клиент подключен: ${conn.remoteSocketAddress}")
                }
            }

            override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
                val logMessage = "Соединение закрыто: $reason"
                Log.d("WebSocket", logMessage)
                LogRepository.log(context, logMessage)

                conn?.let {
                    synchronized(clients) {
                        clients.removeAll { it.webSocket == conn }
                    }
                    showToast(context,"Клиент отключен: ${conn.remoteSocketAddress}")
                }
            }

            override fun onMessage(conn: WebSocket?, message: String?) {
                val logMessage = "Получено сообщение: $message"
                Log.d("WebSocket", logMessage)
                LogRepository.log(context, logMessage)

                if (conn != null && message != null) {
                    try {
                        val msg = message.toMessage()
                        when (msg) {
                            is SystemMessage -> {
                                handleSystemMessage(conn, msg)
                            }
                            is BiometryMessage -> {
                                handleBiometryMessage(conn, msg)
                            }
                            is CommandMessage -> {
                                handleCommandMessage(conn, msg)
                            }
                            else -> {
                                Log.d("WebSocket", "Неизвестный тип сообщения")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        val logMessage = "Ошибка при разборе сообщения: ${e.message}"
                        Log.e("WebSocket", logMessage)
                        LogRepository.log(context, logMessage)

                    }
                }
            }

            override fun onError(conn: WebSocket?, ex: Exception?) {
                val logMessage = "Ошибка: ${ex?.message}"
                Log.e("WebSocket", logMessage)
                LogRepository.log(context, logMessage)
            }

            override fun onStart() {
                val ipAddress = getLocalIpAddress()
                val logMessage = "Сервер запущен на адресе: $ipAddress:$port "

                Log.d("WebSocket", logMessage)
                LogRepository.log(context, logMessage)
            }
        }

        server.setReuseAddr(true)
        server.start()
    }

    private fun handleSystemMessage(conn: WebSocket, msg: SystemMessage) {
        synchronized(clients) {
            val clientInfo = clients.find { it.webSocket == conn }
            if (clientInfo != null) {
                clientInfo.sessionId = msg.sessionId
                clientInfo.typeId = msg.TYPE_ID
                Log.d("WebSocket", "Клиенту присвоены sessionId=${msg.sessionId}, TYPE_ID=${msg.TYPE_ID}")
                LogRepository.log(context, "Клиенту присвоены sessionId=${msg.sessionId}, TYPE_ID=${msg.TYPE_ID}")


                conn.send(SystemMessage(
                    TYPE_ID = msg.TYPE_ID,
                    sessionId = msg.sessionId,
                    STATUS = DeviceStatuses.Waiting.toString(),
                    ERROR_REASON = null
                ))
            }
        }
    }

    private fun handleBiometryMessage(conn: WebSocket, msg: BiometryMessage) {
        val senderClient = clients.find { it.webSocket == conn }
        if (senderClient != null) {
            val sessionId = senderClient.sessionId
            val targetTypeId = 0
            if (sessionId != null) {
                val targetClient = clients.find { it.sessionId == sessionId && it.typeId == targetTypeId }
                if (targetClient != null) {
                     conn.send(DataMessage(
                        Data = listOf(
                            DataBlock(
                                CHUNK_NUMBER = 2,
                                BPM = 67,
                                SONG_ID = "1",
                                BLOCKS = emptyList()
                            ),
                            DataBlock(
                                CHUNK_NUMBER = 3,
                                BPM = 100,
                                SONG_ID = "4",
                                BLOCKS = emptyList()
                            )
                        )
                    ))

                    Log.d("WebSocket", "Отправлено сообщение Data клиенту TYPE_ID=0")
                    LogRepository.log(context, "Отправлено сообщение Data клиенту TYPE_ID=0")
                } else {
                    Log.d("WebSocket", "Клиент с TYPE_ID=0 не найден в сессии $sessionId")
                    LogRepository.log(context, "Клиент с TYPE_ID=0 не найден в сессии $sessionId")
                    conn.send(SystemMessage(
                        TYPE_ID = null,
                        ERROR_REASON = "No vr-device found for this session id",
                        sessionId = null,
                        STATUS =  null,
                    ))
                }
            } else {
                conn.send(SystemMessage(
                    TYPE_ID = null,
                    ERROR_REASON = "SessionId is null, please register device",
                    sessionId = null,
                    STATUS = null
                ))

            }
        }
    }

    private fun handleCommandMessage(conn: WebSocket, msg: CommandMessage) {
        // Реализуйте логику обработки команд
    }

    fun stopServer() {
        try {
            server.stop()
        } catch (e: Exception) {
            e.printStackTrace()
            LogRepository.log(context, e.stackTraceToString())

        }
    }
}