package thong.kotlin.pomodoro.core.utils

inline fun <reified T : Enum<T>> String?.toEnumOrDefault(defaultValue: T): T {
    return this
        ?.let { value -> enumValues<T>().firstOrNull { it.name == value } }
        ?: defaultValue
}