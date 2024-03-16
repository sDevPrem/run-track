buildscript {
    val hilt_version by extra { "2.49" }
    val nav_version by extra { "2.7.6" }
    val room_version by extra { "2.6.1" }
    val paging_version by extra { "3.2.0" }
    val lifecycle_version by extra { "2.6.1" }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val hilt_version: String by extra

    id("com.android.application") version "8.0.0" apply false
    id("com.android.library") version "8.0.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.google.dagger.hilt.android") version hilt_version apply false
}