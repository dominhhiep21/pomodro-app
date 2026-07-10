import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "thong.kotlin.pomodoro.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Exe,
                TargetFormat.Deb,
                TargetFormat.Rpm
            )
            packageName = "aura-pomodoro"
            packageVersion = "1.0.0"
            description = "Aura Pomodoro - Focus timer application"
            vendor = "Thong Kotlin"

            modules("java.sql")

            windows {
                menu = true
                shortcut = true
                perUserInstall = true
                console = true

                // iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
            }

            linux {
                debMaintainer = "email@example.com"

                // iconFile.set(project.file("src/jvmMain/resources/icon.png"))
            }
        }
    }
}