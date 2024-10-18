package com.exam.cardioserversimulator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {

    private var isServiceRunning by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebSocketApp()
        }
    }

    @Composable
    fun WebSocketApp() {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        val logsList = remember { mutableStateListOf<String>() }
        val scrollState = rememberScrollState()

        // Собираем логи в LaunchedEffect
        LaunchedEffect(Unit) {
            LogRepository.logsFlow.collect { logMessage ->
                logsList.add(logMessage)
                // Анимированная прокрутка
                scrollState.animateScrollTo(logsList.size * 20)
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Button(
                onClick = {
                    startWebSocketService()
                },
                enabled = !isServiceRunning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Запустить сервис")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    stopWebSocketService()
                },
                enabled = isServiceRunning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Остановить сервис")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Color.Gray)
                    .padding(8.dp)
            ) {
                Text(
                    text = logsList.joinToString("\n"),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                )
            }
        }
    }

    private fun startWebSocketService() {
        val intent = Intent(this, WebSocketService::class.java)
        startService(intent)
        isServiceRunning = true
    }

    private fun stopWebSocketService() {
        val intent = Intent(this, WebSocketService::class.java)
        stopService(intent)
        isServiceRunning = false
    }
}
