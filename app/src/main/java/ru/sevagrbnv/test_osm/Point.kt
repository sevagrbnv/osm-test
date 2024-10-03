package ru.sevagrbnv.test_osm

import org.osmdroid.util.GeoPoint

data class Point(
    val id: Long,
    val geoPoint: GeoPoint,
    val title: String,
    val connection: String,
    val date: String,
    val time: String,
    val drawable: Int
)