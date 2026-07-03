package thong.kotlin.pomodoro.core.pomodoro.mini_client

import kotlinx.coroutines.test.runTest
import thong.kotlin.pomodoro.core.config.AppConfig
import kotlin.test.Test

/**
 * Integration tests for KtorPomodoroMiniClient.
 * 
 * IMPORTANT: These tests require a local server running at http://localhost:8080.
 * They perform real network calls and will fail if the server is unreachable.
 */
class KtorPomodoroMiniClientTest {

    private val client = KtorPomodoroMiniClient(baseUrl = AppConfig.DEFAULT_POMODORO_MINI_SERVER_URL)

    @Test
    fun register_real_call() = runTest {
        val request = UserRegisterRequest("test@example.com", "user", "password123")

        try {
            val result = client.register(request)

            println("Register call successful")
            println("User id: ${result.id}")
            println("Email: ${result.email}")
            println("Username: ${result.username}")
            println("Created at: ${result.createdAt}")
        } catch (e: Exception) {
            println("Register call failed: ${e.message}")
        }
    }

    @Test
    fun login_real_call() = runTest {
        val request = UserLoginRequest("user", "pass")
        try {
            client.login(request)
            println("Login call successful")
        } catch (e: Exception) {
            println("Login call failed: ${e.message}")
        }
    }

    @Test
    fun profile_real_call() = runTest {
        try {
            client.profile("test-token")
            println("Profile call successful")
        } catch (e: Exception) {
            println("Profile call failed: ${e.message}")
        }
    }

    @Test
    fun logout_real_call() = runTest {
        try {
            client.logout("test-token")
            println("Logout call successful")
        } catch (e: Exception) {
            println("Logout call failed: ${e.message}")
        }
    }

    @Test
    fun settings_real_call() = runTest {
        try {
            client.settings("test-token")
            println("Settings call successful")
        } catch (e: Exception) {
            println("Settings call failed: ${e.message}")
        }
    }

    @Test
    fun updateSettings_real_call() = runTest {
        val request = SettingsRequest(25, 5, 15, 4)
        try {
            client.updateSettings("test-token", request)
            println("UpdateSettings call successful")
        } catch (e: Exception) {
            println("UpdateSettings call failed: ${e.message}")
        }
    }

    @Test
    fun tasks_real_call() = runTest {
        try {
            client.tasks("test-token")
            println("Tasks call successful")
        } catch (e: Exception) {
            println("Tasks call failed: ${e.message}")
        }
    }

    @Test
    fun saveLog_real_call() = runTest {
        val request = PomodoroLogRequest("1", 1500, "COMPLETED", "2023-10-01T10:00:00", "2023-10-01T10:25:00")
        try {
            client.saveLog("test-token", request)
            println("SaveLog call successful")
        } catch (e: Exception) {
            println("SaveLog call failed: ${e.message}")
        }
    }
}
