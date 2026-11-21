package com.offlinenav.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offlinenav.core.theme.*
import com.offlinenav.data.search.CategoryMapper
import com.offlinenav.domain.model.Location
import com.offlinenav.presentation.map.components.MapLibreView

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToSpeedometer: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRoutePreview: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Real Live MapLibre Map View centered on India
        MapLibreView(
            modifier = Modifier.fillMaxSize(),
            centerLocation = Location(28.6139, 77.2090, "India"),
            zoom = 6.0,
            onMapClick = { onNavigateToMap() }
        )

        // Top Embossed Search Floating Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .emboss(shape = RoundedCornerShape(20.dp), elevation = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .clickable { onNavigateToSearch() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search offline places, addresses...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Category Filter Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(CategoryMapper.PRESET_CATEGORIES.take(5)) { cat ->
                    Row(
                        modifier = Modifier
                            .emboss(shape = RoundedCornerShape(14.dp), elevation = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { onNavigateToSearch() }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cat.displayName,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Floating Quick Action Controls (Speedometer & Offline Maps)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EmbossedIconButton(
                onClick = onNavigateToSpeedometer,
                elevation = 6.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Speedometer",
                    tint = AmberGold,
                    modifier = Modifier.size(26.dp)
                )
            }

            EmbossedIconButton(
                onClick = onNavigateToDownloads,
                elevation = 6.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Downloads",
                    tint = CyanPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Bottom Embossed Sheet: Quick Destinations
        EmbossedCard(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = 10.dp
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Navigation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "100% Offline",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SignalGreen
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Home Shortcut
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .deboss(shape = RoundedCornerShape(16.dp))
                            .clickable { onNavigateToRoutePreview("home") }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(CyanPrimary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Home, null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Home", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Navigate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Work Shortcut
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .deboss(shape = RoundedCornerShape(16.dp))
                            .clickable { onNavigateToRoutePreview("work") }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(AmberGold.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Work, null, tint = AmberGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Work", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Navigate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
