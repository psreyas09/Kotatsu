package org.koitharu.kotatsu.desktop.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.desktop.core.di.AppContainer
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    appContainer: AppContainer,
    manga: Manga,
    onBack: () -> Unit,
    onChapterClick: (MangaChapter) -> Unit
) {
    var mangaDetails by remember { mutableStateOf(manga) }
    var chapters by remember { mutableStateOf<List<MangaChapter>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(manga) {
        isLoading = true
        errorMessage = null
        try {
            // Get full manga details with chapters
            mangaDetails = appContainer.mangaRepository.getMangaDetails(manga)
            chapters = appContainer.mangaRepository.getChapters(mangaDetails)
        } catch (e: Exception) {
            errorMessage = e.message ?: "Failed to load manga details"
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { 
                Text(
                    text = mangaDetails.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        )
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading manga details...")
                    }
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Error",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                errorMessage!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = onBack) {
                                Text("Go Back")
                            }
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Manga info section
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = mangaDetails.title,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                
                                if (mangaDetails.rating >= 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "⭐ ${String.format("%.1f", mangaDetails.rating)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                
                                if (mangaDetails.author?.isNotEmpty() == true) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Author: ${mangaDetails.author}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                if (mangaDetails.description?.isNotEmpty() == true) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = mangaDetails.description!!,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                
                                if (mangaDetails.tags.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tags: ${mangaDetails.tags.joinToString(", ") { it.title }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    
                    // Chapters header
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Chapters (${chapters.size})",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Chapters list
                    if (chapters.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No chapters available",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(chapters) { chapter ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onChapterClick(chapter) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = chapter.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        if (chapter.number > 0) {
                                            Text(
                                                text = "Chapter ${chapter.number}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Read",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
