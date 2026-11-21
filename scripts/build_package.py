#!/usr/bin/env python3
import os
import sys
import argparse
import subprocess
import json

def run_command(cmd, desc):
    print(f"[*] {desc}...")
    res = subprocess.run(cmd, shell=True)
    if res.returncode != 0:
        print(f"[!] Error during {desc}")
        sys.exit(res.returncode)

def main():
    parser = argparse.ArgumentParser(description="Build complete offline navigation regional package from OSM PBF")
    parser.add_argument("--region", required=True, help="Unique region ID (e.g. in_west_bengal)")
    parser.add_argument("--name", required=True, help="Human-readable region name")
    parser.add_argument("--pbf", required=True, help="Path to input .osm.pbf file")
    parser.add_argument("--output", default="./output", help="Output directory")
    args = parser.parse_args()

    region_dir = os.path.join(args.output, args.region)
    os.makedirs(region_dir, exist_ok=True)

    tiles_path = os.path.join(region_dir, "tiles.mbtiles")
    search_db_path = os.path.join(region_dir, "search.db")
    valhalla_dir = os.path.join(region_dir, "valhalla_tiles")

    # 1. Generate Vector Tiles using Tilemaker
    run_command(f"tilemaker {args.pbf} --output {tiles_path} --compact", "Generating Vector Tiles (MBTiles)")

    # 2. Extract POIs to SQLite FTS5 Database
    run_command(f"python3 extract_places.py --pbf {args.pbf} --output {search_db_path}", "Extracting POIs & Building FTS5 Search Database")

    # 3. Build Valhalla Routing Graph
    os.makedirs(valhalla_dir, exist_ok=True)
    run_command(f"valhalla_build_tiles -c valhalla.json {args.pbf}", "Building Valhalla Routing Graph")

    # 4. Generate Metadata
    metadata = {
        "id": args.region,
        "name": args.name,
        "map_size_bytes": os.path.getsize(tiles_path) if os.path.exists(tiles_path) else 0,
        "search_size_bytes": os.path.getsize(search_db_path) if os.path.exists(search_db_path) else 0,
        "has_routing": os.path.exists(valhalla_dir),
        "version": "1.0.0"
    }

    with open(os.path.join(region_dir, "metadata.json"), "w") as f:
        json.dump(metadata, f, indent=2)

    print(f"[✓] Successfully packaged {args.name} into {region_dir}")

if __name__ == "__main__":
    main()
