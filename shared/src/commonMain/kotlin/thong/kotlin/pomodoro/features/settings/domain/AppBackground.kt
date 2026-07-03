package thong.kotlin.pomodoro.features.settings.domain

import org.jetbrains.compose.resources.DrawableResource

data class AppBackground(
    val id: String,
    val name: String,
    val resource: DrawableResource,
    val landscapeResource: DrawableResource? = null
)
