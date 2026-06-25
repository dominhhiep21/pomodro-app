plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvmToolchain(17)
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    /*
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    */
    
    androidLibrary {
       namespace = "thong.kotlin.pomodoro.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.cio)
        }
        jvmMain.dependencies {
            implementation(libs.jlayer)
            implementation(libs.ktor.client.cio)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(compose.materialIconsExtended)
            implementation(libs.multiplatformSettings)
            implementation(libs.multiplatformSettings.no.arg)
            implementation(libs.multiplatformSettings.coroutines)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transition)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.lifecycle.kmp)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        /*
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
        */
    }
}

//sqldelight {
//    databases {
//        create("AuraDatabase") {
//            // Package chứa các class Kotlin được tự động sinh ra
//            packageName.set("thong.kotlin.pomodoro.database")
//        }
//    }
//}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}