package com.exam.cardioserversimulator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

class WebSocketService : LifecycleService() {

    private var serverThread: ServerSocketThread? = null

    override fun onCreate() {
        super.onCreate()
        LogRepository.startNewLogFile(applicationContext)
        startServer()
        startForegroundService()
    }

    private fun startServer() {
        serverThread = ServerSocketThread(applicationContext)
        serverThread?.start()
    }

    private fun stopServer() {
        serverThread?.stopServer()
        serverThread = null
    }

    private fun startForegroundService() {
        // Создаем канал уведомлений для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_service_channel"
            val channelName = "My Foreground Service"
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Создаем уведомление
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, "my_service_channel")
            .setContentTitle("Service Running")
            .setContentText("My service is running in the foreground")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Иконка сервиса
            .setContentIntent(pendingIntent)
            .build()

        // Запускаем сервис в переднем плане с уведомлением
        startForeground(1, notification)
    }


    override fun onDestroy() {
        stopServer()
        super.onDestroy()
    }

}
