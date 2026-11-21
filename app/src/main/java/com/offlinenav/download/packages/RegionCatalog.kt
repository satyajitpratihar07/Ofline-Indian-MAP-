package com.offlinenav.download.packages

import com.offlinenav.domain.model.DownloadRegion
import com.offlinenav.domain.model.RegionType

object RegionCatalog {

    val WORLD_REGIONS: List<DownloadRegion> = listOf(
        // INDIA
        DownloadRegion("in_all", "India (Full Country)", "Asia", RegionType.COUNTRY, 6.7, 68.1, 35.5, 97.4, 1_850_000_000L),
        DownloadRegion("in_west_bengal", "West Bengal", "India", RegionType.STATE, 21.5, 85.8, 27.2, 89.9, 210_000_000L),
        DownloadRegion("in_kolkata", "Kolkata City", "West Bengal", RegionType.CITY, 22.45, 88.25, 22.65, 88.45, 45_000_000L),
        DownloadRegion("in_maharashtra", "Maharashtra", "India", RegionType.STATE, 15.6, 72.6, 22.0, 80.9, 340_000_000L),
        DownloadRegion("in_mumbai", "Mumbai Metro", "Maharashtra", RegionType.CITY, 18.88, 72.75, 19.30, 73.05, 65_000_000L),
        DownloadRegion("in_delhi", "Delhi NCR", "India", RegionType.STATE, 28.40, 76.85, 28.90, 77.35, 75_000_000L),
        DownloadRegion("in_karnataka", "Karnataka", "India", RegionType.STATE, 11.5, 74.0, 18.5, 78.5, 290_000_000L),
        DownloadRegion("in_bangalore", "Bengaluru Tech Hub", "Karnataka", RegionType.CITY, 12.80, 77.45, 13.15, 77.75, 55_000_000L),

        // UNITED STATES
        DownloadRegion("us_california", "California", "United States", RegionType.STATE, 32.5, -124.5, 42.0, -114.1, 480_000_000L),
        DownloadRegion("us_new_york", "New York", "United States", RegionType.STATE, 40.5, -79.8, 45.0, -71.8, 380_000_000L),
        DownloadRegion("us_nyc", "New York City Metro", "New York", RegionType.CITY, 40.48, -74.28, 40.92, -73.68, 85_000_000L),
        DownloadRegion("us_texas", "Texas", "United States", RegionType.STATE, 25.8, -106.6, 36.5, -93.5, 520_000_000L),

        // EUROPE & UK
        DownloadRegion("uk_all", "United Kingdom", "Europe", RegionType.COUNTRY, 49.8, -8.6, 60.8, 1.8, 850_000_000L),
        DownloadRegion("uk_london", "Greater London", "United Kingdom", RegionType.CITY, 51.28, -0.51, 51.69, 0.33, 95_000_000L),
        DownloadRegion("de_all", "Germany", "Europe", RegionType.COUNTRY, 47.3, 5.8, 55.1, 15.0, 1_100_000_000L),
        DownloadRegion("de_berlin", "Berlin Metro", "Germany", RegionType.CITY, 52.34, 13.08, 52.68, 13.76, 50_000_000L),
        DownloadRegion("fr_paris", "Paris Île-de-France", "France", RegionType.CITY, 48.65, 2.05, 49.05, 2.65, 70_000_000L),

        // ASIA & AUSTRALIA
        DownloadRegion("jp_tokyo", "Tokyo Metropolitan", "Japan", RegionType.CITY, 35.50, 139.50, 35.85, 139.95, 90_000_000L),
        DownloadRegion("au_sydney", "Sydney Coast", "Australia", RegionType.CITY, -34.12, 150.60, -33.55, 151.35, 60_000_000L)
    )
}
