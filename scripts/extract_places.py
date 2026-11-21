#!/usr/bin/env python3
import sys
import argparse
import sqlite3

def init_db(db_path):
    conn = sqlite3.connect(db_path)
    cur = conn.cursor()
    cur.execute("""
        CREATE TABLE IF NOT EXISTS places (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            address TEXT,
            category TEXT,
            lat REAL NOT NULL,
            lng REAL NOT NULL,
            osm_type TEXT,
            osm_id INTEGER
        )
    """)
    cur.execute("""
        CREATE VIRTUAL TABLE IF NOT EXISTS places_fts USING fts5(
            name, address, category, content=places, content_rowid=id
        )
    """)
    conn.commit()
    return conn

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--pbf", required=True)
    parser.add_argument("--output", required=True)
    args = parser.parse_args()

    conn = init_db(args.output)
    print(f"[*] Initialized SQLite FTS5 database at {args.output}")
    # In production, parse PBF nodes with osmium and execute bulk insert
    conn.close()

if __name__ == "__main__":
    main()
