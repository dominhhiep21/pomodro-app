package thong.kotlin.pomodoro.core.notification

import java.awt.Color
import java.awt.EventQueue
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class JvmNotificationManager : NotificationManager {
    private var trayIcon: TrayIcon? = null

    init {
        initializeTrayIcon()
    }

    private fun initializeTrayIcon() {
        if (!SystemTray.isSupported()) {
            println("SystemTray is not supported on this platform.")
            return
        }

        try {
            val tray = SystemTray.getSystemTray()

            val image = createPomodoroTrayIcon()

            trayIcon = TrayIcon(image, "Pomodoro Timer").apply {
                isImageAutoSize = true
            }

            EventQueue.invokeLater {
                try {
                    tray.add(trayIcon)
                } catch (e: Exception) {
                    println("Failed to add tray icon: ${e.message}")
                    trayIcon = null
                }
            }
        } catch (e: Exception) {
            println("Failed to initialize SystemTray: ${e.message}")
            trayIcon = null
        }
    }

//    init {
//        if (SystemTray.isSupported()) {
//            try {
//                val tray = SystemTray.getSystemTray()
//                // Create a 1x1 transparent image as a placeholder icon
//                // A valid image is often required for the TrayIcon to be added successfully
//                val image: Image = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
//
//                trayIcon = TrayIcon(image, "Pomodoro Timer")
//                trayIcon?.isImageAutoSize = true
//                tray.add(trayIcon)
//            } catch (e: Exception) {
//                println("Failed to initialize SystemTray: ${e.message}")
//            }
//        } else {
//            println("SystemTray is not supported on this platform.")
//        }
//    }

    override fun showNotification(title: String, message: String) {
        val icon = trayIcon

        if (icon == null) {
            showFallbackNotification(title, message)
            return
        }

        EventQueue.invokeLater {
            try {
                icon.displayMessage(
                    title,
                    message,
                    TrayIcon.MessageType.INFO
                )
            } catch (e: Exception) {
                println("Failed to display notification: ${e.message}")
                showFallbackNotification(title, message)
            }
        }
    }

    override fun requestPermission() {
        // JVM/Desktop không cần runtime permission như Android 13+
    }

    fun dispose() {
        val icon = trayIcon ?: return

        if (SystemTray.isSupported()) {
            try {
                SystemTray.getSystemTray().remove(icon)
            } catch (e: Exception) {
                println("Failed to remove tray icon: ${e.message}")
            }
        }

        trayIcon = null
    }

    private fun showFallbackNotification(title: String, message: String) {
        println("NOTIFICATION: $title - $message")
    }

    private fun createPomodoroTrayIcon(): Image {
        val size = 32
        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()

        try {
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            )

            // Background circle
            g.color = Color(40, 40, 40, 255)
            g.fillOval(2, 2, 28, 28)

            // Simple raven/crow-like white shape
            g.color = Color.WHITE
            g.fillOval(8, 9, 12, 10)

            // Beak
            val beakX = intArrayOf(18, 28, 18)
            val beakY = intArrayOf(11, 15, 18)
            g.fillPolygon(beakX, beakY, 3)

            // Tail
            val tailX = intArrayOf(9, 3, 10)
            val tailY = intArrayOf(15, 20, 20)
            g.fillPolygon(tailX, tailY, 3)

            // Eye
            g.color = Color.BLACK
            g.fillOval(14, 11, 3, 3)
        } finally {
            g.dispose()
        }

        return image
    }
}
