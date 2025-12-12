package org.koitharu.kotatsu.desktop.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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
    var showControls by remember { mutableStateOf(true) }
    var zoomLevel by remember { mutableStateOf(1f) }
    var fitToScreen by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    
    LaunchedEffect(chapter) {
        isLoading = true
        errorMessage = null
        currentPageIndex = 0
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
    
    LaunchedEffect(currentPageIndex) {
        scrollState.scrollTo(0)
        horizontalScrollState.scrollTo(0)
        showControls = true
    }
    
    fun nextPage() {
        if (currentPageIndex < pages.size - 1) {
            currentPageIndex++
        }
    }
    
    fun previousPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .onPreviewKeyEvent { event ->
                when {
                    event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight -> {
                        scope.launch { nextPage() }
                        true
                    }
                    event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft -> {
                        scope.launch { previousPage() }
                        true
                    }
                    event.type == KeyEventType.KeyDown && event.key == Key.DirectionDown -> {
                        scope.launch {
                            scrollState.animateScrollTo(
                                (scrollState.value + 200).coerceAtMost(scrollState.maxValue)
                            )
                        }
                        true
                    }
                    event.type == KeyEventType.KeyDown && event.key == Key.DirectionUp -> {
                        scope.launch {
                            scrollState.animateScrollTo(
                                (scrollState.value - 200).coerceAtLeast(0)
                            )
                        }
                        true
                    }
                    event.type == KeyEventType.KeyDown && event.key == Key.Spacebar -> {
                        showControls = !showControls
                        true
                    }
                    else -> false
                }
            }
    ) {
        // Top Bar
        if (showControls) {
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
                },
                modifier = Modifier.animateContentSize()
            )
        }
        
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
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
                    // Page content with optimized loading
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .horizontalScroll(horizontalScrollState)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val currentPage = pages[currentPageIndex]
                        val imageModifier = if (fitToScreen) {
                            Modifier.fillMaxWidth()
                        } else {
                            Modifier.fillMaxWidth(zoomLevel)
                        }
                        
                        AsyncImage(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(currentPage.url)
                                .crossfade(durationMillis = 300)
                                .build(),
                            contentDescription = "Page ${currentPageIndex + 1}",
                            modifier = imageModifier
                                .animateContentSize(),
                            contentScale = if (fitToScreen) ContentScale.Fit else ContentScale.FillWidth,
                            onSuccess = { }
                        )
                    }
                    
                    // Bottom controls
                    if (showControls) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            shape = CardDefaults.shape,
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Zoom & Navigation controls
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Zoom controls
                                    Button(
                                        onClick = { 
                                            zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.5f)
                                            fitToScreen = false
                                        },
                                        enabled = zoomLevel > 0.5f,
                                        modifier = Modifier.size(36.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("−", style = MaterialTheme.typography.labelLarge)
                                    }
                                    
                                    Button(
                                        onClick = { 
                                            zoomLevel = 1f
                                            fitToScreen = true
                                        },
                                        modifier = Modifier.widthIn(min = 50.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (fitToScreen) MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Text("Fit")
                                    }
                                    
                                    Button(
                                        onClick = { 
                                            zoomLevel = (zoomLevel + 0.2f).coerceAtMost(3f)
                                            fitToScreen = false
                                        },
                                        enabled = zoomLevel < 3f,
                                        modifier = Modifier.size(36.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("+", style = MaterialTheme.typography.labelLarge)
                                    }
                                    
                                    Text(
                                        String.format("%.0f%%", zoomLevel * 100),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.width(40.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.weight(1f))
                                    
                                    // Navigation
                                    Button(
                                        onClick = { previousPage() },
                                        enabled = currentPageIndex > 0,
                                        modifier = Modifier.weight(0.2f)
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = "Previous",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Prev")
                                    }
                                    
                                    Button(
                                        onClick = { showControls = false },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Menu,
                                            contentDescription = "Toggle",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    
                                    Button(
                                        onClick = { nextPage() },
                                        enabled = currentPageIndex < pages.size - 1,
                                        modifier = Modifier.weight(0.2f)
                                    ) {
                                        Text("Next")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = "Next",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                
                                // Page slider
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "${currentPageIndex + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.width(28.dp)
                                    )
                                    Slider(
                                        value = currentPageIndex.toFloat(),
                                        onValueChange = { currentPageIndex = it.toInt() },
                                        valueRange = 0f..(pages.size - 1).toFloat(),
                                        modifier = Modifier.weight(1f),
                                        steps = pages.size - 2
                                    )
                                    Text(
                                        "${pages.size}",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.width(28.dp)
                                    )
                                }
                                
                                // Help text
                                Text(
                                    "← → arrow keys to navigate • ↑ ↓ to scroll • Space to toggle controls • − + to zoom • Fit to auto-fit",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
