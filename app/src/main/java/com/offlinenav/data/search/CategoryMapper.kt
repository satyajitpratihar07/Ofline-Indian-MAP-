package com.offlinenav.data.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryMapper {
    data class CategoryInfo(
        val key: String,
        val displayName: String,
        val icon: ImageVector
    )

    val PRESET_CATEGORIES = listOf(
        CategoryInfo("restaurant", "Restaurants", Icons.Default.Restaurant),
        CategoryInfo("fuel", "Gas / Fuel", Icons.Default.LocalGasStation),
        CategoryInfo("hospital", "Hospitals", Icons.Default.LocalHospital),
        CategoryInfo("hotel", "Hotels", Icons.Default.Hotel),
        CategoryInfo("atm", "ATMs / Banks", Icons.Default.LocalAtm),
        CategoryInfo("pharmacy", "Pharmacies", Icons.Default.LocalPharmacy),
        CategoryInfo("supermarket", "Supermarkets", Icons.Default.ShoppingCart),
        CategoryInfo("bus_station", "Transit / Bus", Icons.Default.DirectionsBus),
        CategoryInfo("railway_station", "Train Stations", Icons.Default.Train),
        CategoryInfo("airport", "Airports", Icons.Default.Flight)
    )

    fun getDisplayName(osmTag: String): String {
        return PRESET_CATEGORIES.firstOrNull { it.key.equals(osmTag, ignoreCase = true) }?.displayName
            ?: osmTag.replace('_', ' ').replaceFirstChar { it.uppercase() }
    }
}
