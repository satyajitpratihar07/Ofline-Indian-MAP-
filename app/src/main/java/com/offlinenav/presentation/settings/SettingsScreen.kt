package com.offlinenav.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.offlinenav.core.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToVoiceSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

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
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        EmbossedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column {
                Text("Map & Visual Theme", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = CyanPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Night / Dark Mode")
                    Switch(
                        checked = state.isDarkMode == true,
                        onCheckedChange = { viewModel.setDarkMode(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("3D Architectural Buildings")
                    Switch(
                        checked = state.is3DBuildingsEnabled,
                        onCheckedChange = { viewModel.set3DBuildings(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        EmbossedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column {
                Text("Routing Preferences", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = CyanPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Avoid Toll Roads")
                    Switch(
                        checked = state.avoidTolls,
                        onCheckedChange = { viewModel.setAvoidTolls(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Avoid Motorways / Highways")
                    Switch(
                        checked = state.avoidHighways,
                        onCheckedChange = { viewModel.setAvoidHighways(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation to Voice Settings & About
        EmbossedButton(
            onClick = onNavigateToVoiceSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voice Guidance Settings", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(10.dp))

        EmbossedButton(
            onClick = onNavigateToAbout,
            backgroundColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("About & OpenStreetMap Attribution", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
