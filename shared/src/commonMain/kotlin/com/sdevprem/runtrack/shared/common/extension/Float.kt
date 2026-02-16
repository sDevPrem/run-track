package com.sdevprem.runtrack.shared.common.extension

import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.roundTo(decimals: Int): Float {
    val multiplier = 10f.pow(decimals)
    return (this * multiplier).roundToInt() / multiplier
}