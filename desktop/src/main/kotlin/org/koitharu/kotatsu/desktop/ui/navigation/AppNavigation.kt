package org.koitharu.kotatsu.desktop.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koitharu.kotatsu.desktop.core.di.AppContainer
import org.koitharu.kotatsu.desktop.ui.screens.*

enum class Screen(val title: String, val route: String) {
    HOME("Home", "home"),
    LIBRARY("Library", "library"),
    BROWSE("Browse", "browse"),
    HISTORY("History", "history"),
    SETTINGS("Settings", "settings")
}

@Composable
fun AppNavigation(appContainer: AppContainer) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    
    Row(modifier = Modifier.fillMaxSize()) {
        // Navigation Rail (Side menu)
        NavigationRail {
            Screen.entries.forEach { screen ->
                NavigationRailItem(
                    icon = { Icon(getScreenIcon(screen), contentDescription = screen.title) },
                    label = { Text(screen.title) },
                    selected = currentScreen == screen,
                    onClick = { currentScreen = screen }
                )
            }
        }
        
        // Main content
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(appContainer)
                Screen.LIBRARY -> LibraryScreen(appContainer)
                Screen.BROWSE -> BrowseScreen(appContainer)
                Screen.HISTORY -> HistoryScreen(appContainer)
                Screen.SETTINGS -> SettingsScreen(appContainer)
            }
        }
    }
}

@Composable
private fun getScreenIcon(screen: Screen) = when (screen) {
    Screen.HOME -> Icons.Default.Home
    Screen.LIBRARY -> Icons.Default.Favorite
    Screen.BROWSE -> Icons.Default.Explore
    Screen.HISTORY -> Icons.Default.History
    Screen.SETTINGS -> Icons.Default.Settings
}
