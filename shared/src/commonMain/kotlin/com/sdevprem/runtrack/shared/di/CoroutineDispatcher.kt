package com.sdevprem.runtrack.shared.di

import kotlinx.coroutines.CoroutineDispatcher

class CoroutineDispatchers(
    val io: CoroutineDispatcher,
    val main: CoroutineDispatcher,
    val mainImmediate: CoroutineDispatcher,
    val default: CoroutineDispatcher
)