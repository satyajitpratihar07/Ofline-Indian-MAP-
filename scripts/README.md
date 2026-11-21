# OfflineNav Map Data Processing Pipeline

This pipeline processes raw OpenStreetMap extracts (`.osm.pbf`) into 100% offline regional packages for the OfflineNav Android application.

## Prerequisites
Ensure the following CLI tools are installed on your Linux / macOS / WSL host:
- `osmium-tool` (`sudo apt install osmium-tool`)
- `tilemaker` (https://github.com/systemed/tilemaker)
- `valhalla` (https://github.com/valhalla/valhalla)
- `sqlite3` (`sudo apt install sqlite3`)
- `python3` with `pip install osmium`

## Usage
1. Download regional OpenStreetMap PBF:
   ```bash
   wget https://download.geofabrik.de/asia/india/west-bengal-latest.osm.pbf
   ```

2. Run the packaging pipeline:
   ```bash
   python3 build_package.py \
       --region in_west_bengal \
       --name "West Bengal" \
       --pbf west-bengal-latest.osm.pbf \
       --output ./dist/packages/
   ```

3. The generated package directory will contain:
   - `tiles.mbtiles` (Vector tiles with zoom 0-14)
   - `search.db` (SQLite database with FTS5 virtual table for POI/address search)
   - `valhalla_tiles/` (Valhalla routing graph tiles)
   - `metadata.json` (Bounding box, checksum, and file sizes)
