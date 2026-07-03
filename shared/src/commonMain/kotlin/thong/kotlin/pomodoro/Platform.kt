package thong.kotlin.pomodoro

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform