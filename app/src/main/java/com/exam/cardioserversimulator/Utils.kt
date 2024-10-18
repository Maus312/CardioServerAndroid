package com.exam.cardioserversimulator

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import java.net.InetAddress
import java.net.NetworkInterface

fun getLocalIpAddress(): String? {
    try {
        val interfaces: List<NetworkInterface> = NetworkInterface.getNetworkInterfaces().toList()
        for (networkInterface in interfaces) {
            val addresses: List<InetAddress> = networkInterface.inetAddresses.toList()
            for (address in addresses) {
                // Check for IPv4 address and ensure it's not a loopback address
                if (!address.isLoopbackAddress && address is java.net.Inet4Address) {
                    return address.hostAddress
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun vibrateDevice(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(200)
    }
}

fun showToast(context: Context,message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
