package ru.sevagrbnv.test_osm

import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class MarkerInfoWindow(private val mapView: MapView) :
    InfoWindow(R.layout.info_window, mapView) {

    override fun onOpen(item: Any?) {
        closeAllInfoWindowsOn(mapView)
    }

    override fun onClose() {
        close()
    }
}