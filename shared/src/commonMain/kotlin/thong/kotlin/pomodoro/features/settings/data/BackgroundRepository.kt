package thong.kotlin.pomodoro.features.settings.data

import pomodrokotlin.shared.generated.resources.Res
import pomodrokotlin.shared.generated.resources.cyberpunk_bg
import pomodrokotlin.shared.generated.resources.forest_bg
import pomodrokotlin.shared.generated.resources.landscape_cyberpunk_bg
import pomodrokotlin.shared.generated.resources.landscape_forest_bg
import pomodrokotlin.shared.generated.resources.landspace_startup_bg
import pomodrokotlin.shared.generated.resources.lanscape_ocean_bg
import pomodrokotlin.shared.generated.resources.ocean_bg
import pomodrokotlin.shared.generated.resources.startup_bg
import thong.kotlin.pomodoro.features.settings.domain.AppBackground

object BackgroundRepository {
    val availableBackgrounds = listOf(
        AppBackground("cyberpunk", "Cyberpunk", Res.drawable.cyberpunk_bg, Res.drawable.landscape_cyberpunk_bg),
        AppBackground("forest", "Rừng già", Res.drawable.forest_bg, Res.drawable.landscape_forest_bg),
        AppBackground("ocean", "Đại dương", Res.drawable.ocean_bg, Res.drawable.lanscape_ocean_bg),
        AppBackground("classic", "Mặc định", Res.drawable.startup_bg, Res.drawable.landspace_startup_bg)
    )

    const val DEFAULT_BACKGROUND_ID = "classic"

    fun getNameById(id: String?): String {
        return availableBackgrounds.find { it.id == id }?.name ?: "Mặc định"
    }
}
