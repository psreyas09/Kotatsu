package org.koitharu.kotatsu.desktop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.desktop.core.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(appContainer: AppContainer) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Top bar
        TopAppBar(
            title = { Text("Kotatsu - Manga Reader", style = MaterialTheme.typography.headlineMedium) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Welcome section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    "Welcome to Kotatsu Desktop",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Your manga reading experience on Linux",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick actions
        Text("Quick Actions", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { /* TODO: Navigate to browse */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Browse Manga Sources")
            }
            
            Button(
                onClick = { /* TODO: Navigate to library */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("View Library")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Recent updates section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Recent Updates",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Your recently updated manga will appear here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
