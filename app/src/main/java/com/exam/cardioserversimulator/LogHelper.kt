package com.exam.cardioserversimulator

import android.content.Context
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.IOException

object LogRepository {
    private val _logsFlow = MutableSharedFlow<String>(replay = 100, extraBufferCapacity = 100)
    val logsFlow: SharedFlow<String> = _logsFlow.asSharedFlow()

    private var currentLogFileName: String? = null

    fun startNewLogFile(context: Context) {
        currentLogFileName = "websocket_logs_${System.currentTimeMillis()}.txt"
    }

    fun log(context: Context, message: String) {
        _logsFlow.tryEmit(message)
        writeLogToFile(context, message)
    }

    private fun writeLogToFile(context: Context, message: String) {
        try {
            val fileName = currentLogFileName ?: "websocket_logs.txt"
            context.openFileOutput(fileName, Context.MODE_APPEND).use { fos ->
                fos.write((message + "\n").toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readLogs(context: Context): String {
        return try {
            val fileName = currentLogFileName ?: "websocket_logs.txt"
            context.openFileInput(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
}
