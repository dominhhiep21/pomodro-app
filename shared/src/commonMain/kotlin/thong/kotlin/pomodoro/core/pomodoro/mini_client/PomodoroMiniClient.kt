package thong.kotlin.pomodoro.core.pomodoro.mini_client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import thong.kotlin.pomodoro.features.pomodoro._base.domain.model.UserSettings

interface PomodoroMiniClient {
    suspend fun register(userRegisterRequest: UserRegisterRequest): UserResponse
    suspend fun login(userLoginRequest: UserLoginRequest)
    suspend fun profile(token: String)
    suspend fun logout(token: String)
    suspend fun settings(token: String)
    suspend fun updateSettings(token: String, request: SettingsRequest)
    suspend fun tasks(token: String)
    suspend fun saveLog(token: String, logRequest: PomodoroLogRequest)
}

class KtorPomodoroMiniClient(
    private val baseUrl: String = "http://localhost:8999",
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }
) : PomodoroMiniClient {

    override suspend fun register(userRegisterRequest: UserRegisterRequest): UserResponse {
        val response = client.post("$baseUrl/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userRegisterRequest)
        }

        return when (response.status) {
            HttpStatusCode.Created -> response.body<UserResponse>()

            HttpStatusCode.BadRequest,
            HttpStatusCode.Conflict,
            HttpStatusCode.InternalServerError -> {
                val error = response.body<ErrorResponse>()
                throw Exception(error.error) as Throwable
            }

            else -> {
                throw Exception("Unexpected status: ${response.status}")
            }
        }
    }

    override suspend fun login(userLoginRequest: UserLoginRequest) {
        return client.post("$baseUrl/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(userLoginRequest)
        }.body()
    }

    override suspend fun profile(token: String) {
        return client.get("$baseUrl/api/users/profile") {
            header("Authorization", "Bearer $token")
        }.body()
    }

    override suspend fun logout(token: String) {
        return client.get("$baseUrl/api/users/logout") {
            header("Authorization", "Bearer $token")
        }.body()
    }

    override suspend fun settings(token: String) {
        return client.get("$baseUrl/api/settings") {
            header("Authorization", "Bearer $token")
        }.body()
    }

    override suspend fun updateSettings(
        token: String,
        request: SettingsRequest
    ) {
        return client.put("$baseUrl/api/settings") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun tasks(token: String) {
        return client.get("$baseUrl/api/tasks") {
            header("Authorization", "Bearer $token")
        }.body()
    }

    override suspend fun saveLog(
        token: String,
        logRequest: PomodoroLogRequest
    ) {
        return client.post("$baseUrl/api/pomodoros/log") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(logRequest)
        }.body()
    }

    fun dispose() {
        client.close()
    }
}
