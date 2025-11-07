package org.koitharu.kotatsu.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.desktop.ui.navigation.AppNavigation
import org.koitharu.kotatsu.desktop.ui.theme.KotatsuTheme
import org.koitharu.kotatsu.desktop.core.di.AppContainer
import org.koitharu.kotatsu.desktop.core.di.AppContainerImpl

/**
 * Main entry point for Kotatsu Desktop Application
 */
fun main() = application {
    // Initialize app container (manual DI)
    val appContainer = remember { AppContainerImpl() }
    
    // Cleanup on exit
    DisposableEffect(Unit) {
        onDispose {
            appContainer.cleanup()
        }
    }
    
    val windowState = rememberWindowState(
        width = 1280.dp,
        height = 800.dp
    )
    
    Window(
        onCloseRequest = {
            appContainer.cleanup()
            exitApplication()
        },
        title = "Kotatsu - Manga Reader",
        state = windowState
    ) {
        KotatsuTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavigation(appContainer)
            }
        }
    }
}
