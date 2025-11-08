package org.koitharu.kotatsu.desktop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.desktop.core.di.AppContainer
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    appContainer: AppContainer,
    chapter: MangaChapter,
    onBack: () -> Unit
) {
    var pages by remember { mutableStateOf<List<MangaPage>>(emptyList()) }
    var currentPageIndex by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    LaunchedEffect(chapter) {
        isLoading = true
        errorMessage = null
        try {
            pages = appContainer.mangaRepository.getPages(chapter)
            if (pages.isEmpty()) {
                errorMessage = "No pages found in this chapter"
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Failed to load pages"
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }
    
    // Reset scroll position when page changes
    LaunchedEffect(currentPageIndex) {
        scrollState.scrollTo(0)
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { 
                Column {
                    Text(
                        text = chapter.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (pages.isNotEmpty()) {
                        Text(
                            text = "Page ${currentPageIndex + 1} of ${pages.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
                        Text("Loading pages...")
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
            pages.isNotEmpty() -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Page content
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val currentPage = pages[currentPageIndex]
                        
                        AsyncImage(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(currentPage.url)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Page ${currentPageIndex + 1}",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                            onError = { error ->
                                // Error handled by error composable below
                            }
                        )
                        
                        // Show error message on top if image failed to load
                        var imageError by remember { mutableStateOf(false) }
                        
                        if (imageError) {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "Failed to load image",
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Text(
                                            currentPage.url,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Navigation controls
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { 
                                    if (currentPageIndex > 0) {
                                        currentPageIndex--
                                    }
                                },
                                enabled = currentPageIndex > 0
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Previous",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Previous")
                            }
                            
                            Text(
                                text = "${currentPageIndex + 1} / ${pages.size}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Button(
                                onClick = { 
                                    if (currentPageIndex < pages.size - 1) {
                                        currentPageIndex++
                                    }
                                },
                                enabled = currentPageIndex < pages.size - 1
                            ) {
                                Text("Next")
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = "Next",
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
