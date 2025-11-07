package org.koitharu.kotatsu.desktop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koitharu.kotatsu.desktop.core.di.AppContainer

data class LibraryManga(
    val id: Long,
    val title: String,
    val coverUrl: String,
    val source: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(appContainer: AppContainer) {
    var libraryManga by remember { mutableStateOf<List<LibraryManga>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            try {
                // Load manga from database
                libraryManga = withContext(Dispatchers.IO) {
                    val resultSet = appContainer.database.query(
                        """
                        SELECT m.id, m.title, m.cover_url, m.source
                        FROM manga m
                        INNER JOIN favourites f ON m.manga_id = f.manga_id
                        ORDER BY f.created_at DESC
                        LIMIT 50
                        """.trimIndent()
                    )
                    
                    val list = mutableListOf<LibraryManga>()
                    while (resultSet.next()) {
                        list.add(
                            LibraryManga(
                                id = resultSet.getLong("id"),
                                title = resultSet.getString("title"),
                                coverUrl = resultSet.getString("cover_url"),
                                source = resultSet.getString("source")
                            )
                        )
                    }
                    resultSet.close()
                    list
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TopAppBar(
            title = { Text("Library (${libraryManga.size})") },
            actions = {
                IconButton(onClick = { /* TODO: Add manga */ }) {
                    Icon(Icons.Default.Add, "Add manga")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            libraryManga.isEmpty() -> {
                Card(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "📚",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Your library is empty",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Browse manga sources and add favorites to your library",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(libraryManga) { manga ->
                        LibraryMangaCard(manga)
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryMangaCard(manga: LibraryManga) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Placeholder for cover image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "📖",
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = manga.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = manga.source,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
