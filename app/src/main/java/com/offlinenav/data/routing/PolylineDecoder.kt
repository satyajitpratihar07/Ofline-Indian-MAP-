package com.offlinenav.data.routing

import com.offlinenav.domain.model.Location

object PolylineDecoder {

    /**
     * Decodes an encoded path string into a sequence of [Location]s.
     * Supports precision 5 (Google polyline) and precision 6 (Valhalla polyline6).
     */
    fun decode(encodedPath: String, precision: Int = 6): List<Location> {
        val factor = if (precision == 6) 1e6 else 1e5
        val len = encodedPath.length
        val path = ArrayList<Location>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = encodedPath[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1

            result = 1
            shift = 0
            do {
                b = encodedPath[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1

            path.add(
                Location(
                    latitude = lat / factor,
                    longitude = lng / factor
                )
            )
        }
        return path
    }
}
