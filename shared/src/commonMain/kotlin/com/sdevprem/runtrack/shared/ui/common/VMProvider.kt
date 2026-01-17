package com.sdevprem.runtrack.shared.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.reflect.KClass

val LocalVMProvider = staticCompositionLocalOf<VMProvider> {
    object : VMProvider {
        @Composable
        override fun <T : Any> provideViewModel(kClass: KClass<T>): T {
            throw IllegalStateException("No VMProvider provided")
        }
    }
}

interface VMProvider {

    @Composable
    fun <T : Any> provideViewModel(kClass: KClass<T>): T
}