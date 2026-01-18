plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
//    kotlin("plugin.serialization") version "2.3.0"
}

kotlin {

    androidTarget {
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "shared"
            isStatic = false
        }
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.play.services.location)
                implementation(libs.androidx.hilt.navigation.compose)
                implementation(libs.hilt.android)
                implementation(libs.androidx.lifecycle.service)
                implementation(libs.ktor.client.android)
            }
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.room.paging)
                implementation(libs.androidx.sqlite.bundled)
                api(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.datastore)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.paging.compose.common)
                implementation(compose.components.resources)
                implementation(libs.jetBrains.lifecycle)
                implementation(libs.coil3.compose)
                implementation(libs.coil.network.ktor)
                implementation(libs.compose.backhandler)
                implementation(libs.filekit.core)
//                implementation(libs.filekit.dialogs)
//                implementation(libs.filekit.dialogs.compose)
//                implementation("network.chaintech:cmpfilepicker:1.0.0")

//            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0-RC")
//            implementation(libs.compose.ui)
//            implementation(libs.compose.graphics)
            }
        }
        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {

        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspAndroid", libs.hilt.compiler)
}

android {
    namespace = "com.sdevprem.runtrack.shared"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

//// Make common source sets depend on KSP generated code
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    if (name != "kspCommonMainKotlinMetadata") {
//        dependsOn("kspCommonMainKotlinMetadata")
//    }
//}

