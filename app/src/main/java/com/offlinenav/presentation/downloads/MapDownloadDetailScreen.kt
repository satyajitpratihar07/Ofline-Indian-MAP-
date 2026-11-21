package com.offlinenav.presentation.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.offlinenav.core.theme.*
import com.offlinenav.download.packages.RegionCatalog

@Composable
fun MapDownloadDetailScreen(
    regionId: String,
    viewModel: DownloadViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val region = RegionCatalog.WORLD_REGIONS.firstOrNull { it.id == regionId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmbossedIconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = region?.name ?: "Region Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (region != null) {
            EmbossedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column {
                    Text("Package Components", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("• OpenStreetMap Vector Tiles (MBTiles): ~${(region.sizeBytes * 0.6 / 1024 / 1024).toInt()} MB")
                    Text("• Valhalla Offline Routing Graph: ~${(region.sizeBytes * 0.3 / 1024 / 1024).toInt()} MB")
                    Text("• SQLite FTS5 Search & POI Database: ~${(region.sizeBytes * 0.1 / 1024 / 1024).toInt()} MB")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Total Download Size: ${region.sizeFormatted}", fontWeight = FontWeight.Bold, color = CyanPrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            EmbossedButton(
                onClick = { viewModel.startDownload(region.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download Regional Package", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            EmbossedButton(
                onClick = {
                    viewModel.deleteRegion(region.id)
                    onNavigateBack()
                },
                backgroundColor = SignalRed.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Local Map Data", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
