package com.sdevprem.runtrack.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform