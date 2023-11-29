package com.example.saglife.models
import kotlin.math.*

class Location(val latitude: Double, val longitude: Double) {

    fun distanceEnKm(otherLocation: Location): Double {
        val rayonTerre = 6371.0 // Rayon moyen de la Terre en kilomètres

        val lat1Rad = Math.toRadians(latitude)
        val lon1Rad = Math.toRadians(longitude)
        val lat2Rad = Math.toRadians(otherLocation.latitude)
        val lon2Rad = Math.toRadians(otherLocation.longitude)

        val dLat = lat2Rad - lat1Rad
        val dLon = lon2Rad - lon1Rad

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return rayonTerre * c
    }
}