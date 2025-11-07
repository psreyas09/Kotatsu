package org.koitharu.kotatsu.desktop.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.desktop.core.di.AppContainer
import org.koitharu.kotatsu.desktop.core.manga.MangaInfo
import org.koitharu.kotatsu.desktop.core.manga.MangaSourceInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(appContainer: AppContainer) {
    var selectedSource by remember { mutableStateOf<MangaSourceInfo?>(null) }
    
    if (selectedSource == null) {
        SourceListScreen(appContainer) { source ->
            selectedSource = source
        }
    } else {
        MangaListScreen(appContainer, selectedSource!!) {
            selectedSource = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceListScreen(
    appContainer: AppContainer,
    onSourceSelected: (MangaSourceInfo) -> Unit
) {
    val sources = remember { appContainer.mangaRepository.getAvailableSources() }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredSources = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            sources
        } else {
            sources.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TopAppBar(
            title = { Text("Browse Sources (${sources.size} available)") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search sources...") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Sources list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredSources) { source ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSourceSelected(source) }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = source.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Source: ${source.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "WEB",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaListScreen(
    appContainer: AppContainer,
    source: MangaSourceInfo,
    onBack: () -> Unit
) {
    var mangaList by remember { mutableStateOf<List<MangaInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(source) {
        isLoading = true
        errorMessage = null
        try {
            mangaList = appContainer.mangaRepository.getMangaList(source, 0)
        } catch (e: Exception) {
            errorMessage = e.message ?: "Failed to load manga"
        } finally {
            isLoading = false
        }
    }
    
    fun performSearch() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                mangaList = if (searchQuery.isEmpty()) {
                    appContainer.mangaRepository.getMangaList(source, 0)
                } else {
                    appContainer.mangaRepository.searchManga(source, searchQuery)
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Search failed"
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
            title = { Text(source.name) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search manga...") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            trailingIcon = {
                Button(onClick = { performSearch() }) {
                    Text("Search")
                }
            },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading manga...")
                    }
                }
            }
            errorMessage != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Button(onClick = { performSearch() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            mangaList.isEmpty() -> {
                Card(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No manga found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    items(mangaList) { manga ->
                        MangaCard(manga)
                    }
                }
            }
        }
    }
}

@Composable
fun MangaCard(manga: MangaInfo) {
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
                    color = MaterialTheme.colorScheme.surfaceVariant
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
            
            if (manga.rating > 0) {
                Text(
                    text = "⭐ ${String.format("%.1f", manga.rating)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
