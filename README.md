# 🗺️ OfflineNav — Complete Offline GPS Navigation App for Android

OfflineNav is a production-quality, Google Maps-alternative Android navigation application built from the ground up for **100% offline functionality**. Users can download vector map packages, search points of interest, compute multi-modal routes, and drive with real turn-by-turn voice guidance and live GPS speedometer without an internet connection.

---

## 🌟 Key Features

- **Worldwide Offline Map Engine**: Powered by MapLibre Native and OpenStreetMap (OSM) vector tiles. Full support for smooth 2D overview and 3D architectural perspective.
- **Offline Multi-Modal Routing**: Fast, battery-efficient topological routing engine for **Car**, **Motorcycle**, **Scooter**, **Bicycle**, and **Walking**, with fastest and shortest cost models, plus toll and highway avoidance.
- **Offline Search with SQLite FTS5**: Instant local search for cities, streets, hospitals, petrol/gas stations, hotels, ATMs, restaurants, and transit hubs.
- **Live Turn-by-Turn Voice Navigation**: Smart Android Text-to-Speech announcements at 1 km, 500 m, 200 m, and 50 m before maneuvers.
- **Automatic Offline Rerouting**: Real-time cross-track deviation detection that automatically recalculates routes when taking an alternate road.
- **Digital & Analog GPS Speedometer**: Custom Canvas circular speed gauge with speed zones, moving vs. stationary trip timers, average/max speed, and speed limit alerts.
- **Background Navigation**: Ongoing Android Foreground Service with live turn instructions and ETA notifications while screen is locked or in other apps.
- **Embossed Neumorphic Luxury UI**: Tactile dual-lit controls, soft bevels, glowing night radar, and glare-free OLED midnight theme designed for nighttime driving safety.

---

## 🏗️ System Architecture

```
app/src/main/java/com/offlinenav/
├── core/
│   ├── data/local/         # Room Database (SavedPlace, History, MapRegion)
│   ├── di/                 # Hilt Dependency Injection Modules
│   ├── navigation/         # NavHost & Screen Routes
│   └── theme/              # Embossed Neumorphic Design System & Dark/Night Themes
├── domain/
│   ├── model/              # Domain entities (Location, Route, TurnType, etc.)
│   └── repository/         # Clean Architecture Repository Interfaces
├── data/
│   ├── repository/         # Repository Implementations
│   ├── routing/            # Valhalla & Offline Graph Router
│   └── search/             # SQLite FTS5 Offline Search Engine
├── gps/
│   ├── location/           # Android GNSS & LocationManager
│   ├── mapmatching/        # Polyline Snapping & Deviation Detection
│   ├── speed/              # Speed Calculator & Trip Statistics Tracker
│   └── tracking/           # Location Tracking Foreground Service
├── navigation/
│   ├── engine/             # NavigationEngine & DeviationDetector
│   ├── service/            # NavigationForegroundService
│   └── voice/              # NavigationTtsEngine (Android Text-to-Speech)
├── download/
│   ├── manager/            # WorkManager-based Resumable Map Download Worker
│   ├── packages/           # World Regional Catalog (Continents, Countries, Cities)
│   └── storage/            # MBTiles & SQLite File Storage Manager
└── presentation/           # 17 Complete Jetpack Compose Screens
    ├── splash/             # Embossed Animated Radar Splash Screen
    ├── onboarding/         # 3-Slide Feature Carousel
    ├── home/               # Main Dashboard with Floating Search & Categories
    ├── map/                # MapLibre Map with 2D/3D & Compass
    ├── search/             # Search Input & Split Results Map View
    ├── route/              # Route Preview, Travel Mode Tabs, Toll Toggles
    ├── navigation/         # Live Navigation HUD with Turn Banner
    ├── speedometer/        # High-End Circular Canvas Speedometer Gauge
    ├── downloads/          # Download Manager & Region Details
    ├── saved/              # Home/Work/Favorites Management
    ├── history/            # Trip Logs & Replay
    └── settings/           # Dark/Night Mode, Units, Voice Language & Attribution
```

---

## 🛠️ Technology Stack

| Component | Library / Framework |
|---|---|
| Language | Kotlin (1.9.22) |
| UI Toolkit | Jetpack Compose (BOM 2024.02.00) + Material 3 |
| Map Engine | MapLibre Native Android (11.0.0) |
| Data Source | OpenStreetMap (OSM) Vector Tiles |
| Routing Engine | Valhalla / Offline Graph Router |
| Search Engine | SQLite FTS5 Virtual Tables |
| Database | Android Room (2.6.1) |
| Dependency Injection | Dagger Hilt (2.50) |
| Asynchronous | Kotlin Coroutines & Flow |
| Background Work | Android WorkManager (2.9.0) |
| Network | OkHttp 4.12.0 & Gson |
| Voice Synthesis | Android Text-to-Speech (TTS) |

---

## 📦 Map Data Packaging Pipeline

Server-side scripts located in `scripts/`:
- `build_package.py`: Builds `.mbtiles`, routing graphs, and SQLite search databases from raw `.osm.pbf` files.
- `extract_places.py`: Extracts POIs and generates FTS5 indexed search tables.

---

## 📜 Attribution & Licensing

- **Map Data**: © [OpenStreetMap](https://www.openstreetmap.org/copyright) contributors, under the [ODbL](https://opendatacommons.org/licenses/odbl/).
- **Map Engine**: [MapLibre Native](https://maplibre.org/), BSD 2-Clause License.
- **Routing**: [Valhalla](https://github.com/valhalla/valhalla), MIT License.
