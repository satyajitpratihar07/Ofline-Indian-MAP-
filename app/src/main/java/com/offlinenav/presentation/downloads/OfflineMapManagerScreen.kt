package com.offlinenav.presentation.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.offlinenav.core.theme.*
import com.offlinenav.domain.model.DownloadStatus

@Composable
fun OfflineMapManagerScreen(
    viewModel: DownloadViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmbossedIconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Offline Maps",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Storage Usage Card
        val usedMb = (state.usedStorageBytes / (1024 * 1024)).toInt()
        val freeGb = String.format("%.1f", state.freeStorageBytes / (1024.0 * 1024.0 * 1024.0))

        EmbossedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Offline Storage", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("$usedMb MB used", style = MaterialTheme.typography.labelSmall, color = CyanPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = (usedMb / 5000f).coerceIn(0.05f, 1f),
                    modifier = Modifier.fillMaxWidth(),
                    color = CyanPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("$freeGb GB free space available on device", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Selector (Explore Regions vs Downloaded)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .emboss(shape = RoundedCornerShape(14.dp), elevation = if (selectedTab == 0) 2.dp else 4.dp)
                    .background(if (selectedTab == 0) CyanPrimary else MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                    .clickable { selectedTab = 0 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "All Regions",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .emboss(shape = RoundedCornerShape(14.dp), elevation = if (selectedTab == 1) 2.dp else 4.dp)
                    .background(if (selectedTab == 1) CyanPrimary else MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                    .clickable { selectedTab = 1 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Downloaded (${state.downloadedRegions.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Regions List
        val regionsToDisplay = if (selectedTab == 0) state.availableRegions else state.downloadedRegions

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(regionsToDisplay) { region ->
                EmbossedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToDetail(region.id) },
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = region.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${region.parentName} • ${region.sizeFormatted}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        when (region.status) {
                            DownloadStatus.COMPLETED -> {
                                Box(
                                    modifier = Modifier
                                        .background(SignalGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Downloaded", color = SignalGreen, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                            DownloadStatus.DOWNLOADING -> {
                                CircularProgressIndicator(
                                    progress = region.progress,
                                    modifier = Modifier.size(28.dp),
                                    color = CyanPrimary
                                )
                            }
                            else -> {
                                EmbossedIconButton(
                                    onClick = { viewModel.startDownload(region.id) }
                                ) {
                                    Icon(Icons.Default.Download, null, tint = CyanPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
