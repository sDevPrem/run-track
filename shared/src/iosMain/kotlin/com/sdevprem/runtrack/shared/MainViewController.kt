package com.sdevprem.runtrack.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import androidx.navigation.compose.rememberNavController
import com.sdevprem.runtrack.shared.ui.screen.main.MainScreen
import com.sdevprem.runtrack.shared.ui.theme.AppTheme

fun MainViewController() = ComposeUIViewController {
//    Map(modifier = Modifier, pathPoints = emptyList(), isRunningFinished = false, onSnapshot = {})
    AppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(rememberNavController()) {
//                finish()
            }
        }
    }
}
