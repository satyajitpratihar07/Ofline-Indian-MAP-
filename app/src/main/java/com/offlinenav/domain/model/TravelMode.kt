package com.offlinenav.domain.model

enum class TravelMode(val title: String, val speedKmhAverage: Float) {
    CAR("Car", 50f),
    MOTORCYCLE("Motorcycle", 45f),
    SCOOTER("Scooter", 35f),
    BICYCLE("Bicycle", 18f),
    WALKING("Walking", 5f)
}
