package ru.sevagrbnv.test_osm

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment : Fragment() {

    private var mapView: MapView? = null
    private var locationOverlay: MyLocationNewOverlay? = null

    private val pointList = listOf(
        Point(
            id = 1L,
            geoPoint = GeoPoint(55.7225, 37.6856),
            title = "Илья",
            connection = "GPS",
            date = "02.07.17",
            time = "15:00",
            drawable = R.drawable.custom_marker
        ),
        Point(
            id = 1L,
            geoPoint = GeoPoint(55.7628, 37.6356),
            title = "Илья",
            connection = "GPS",
            date = "02.07.17",
            time = "15:00",
            drawable = R.drawable.custom_marker
        ),
        Point(
            id = 1L,
            geoPoint = GeoPoint(55.7829, 37.6760),
            title = "Илья",
            connection = "GPS",
            date = "02.07.17",
            time = "15:00",
            drawable = R.drawable.custom_marker
        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().load(
            requireActivity(), requireActivity().getSharedPreferences(
                OSM_PREF,
                AppCompatActivity.MODE_PRIVATE
            )
        )

        mapView = requireActivity().findViewById<MapView?>(R.id.map).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            zoomController?.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            setMultiTouchControls(true)
        }

        val mapController = mapView?.controller
        mapController?.setZoom(DEFAULT_ZOOM)

        val startPoint = GeoPoint(55.7522, 37.6156)
        mapController?.setCenter(startPoint)

        pointList.forEach(::setPoint)
        mapView?.invalidate()

        with(requireActivity()) {
            findViewById<FloatingActionButton>(R.id.zoomIn).apply {
                setOnClickListener { mapController?.zoomIn() }
            }

            findViewById<FloatingActionButton>(R.id.zoomOut).apply {
                setOnClickListener { mapController?.zoomOut() }
            }

            findViewById<FloatingActionButton>(R.id.myLocation).apply {
                setOnClickListener { enableMyLocation(checkNotNull(mapController)) }
            }
        }
    }

    private fun setPoint(point: Point) {
        Marker(mapView).apply {
            position = point.geoPoint
            icon = ContextCompat.getDrawable(requireActivity(), point.drawable)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP)
            setInfoWindowAnchor(Marker.ANCHOR_RIGHT, Marker.ANCHOR_BOTTOM)
            mapView?.overlays?.add(this)
            infoWindow = MarkerInfoWindow(checkNotNull(mapView))

            setOnMarkerClickListener { clickedMarker, _ ->

                if (!clickedMarker.isInfoWindowShown)
                    clickedMarker.showInfoWindow()
                else clickedMarker.closeInfoWindow()


                val bottomSheetFragment = BottomSheet()
                bottomSheetFragment.show(
                    requireActivity().supportFragmentManager,
                    bottomSheetFragment.tag
                )
                true
            }
        }
    }

    private fun enableMyLocation(mapController: IMapController) {
        if (hasLocationPermissions()) {
            locationOverlay = MyLocationNewOverlay(mapView)
            locationOverlay?.enableMyLocation()
            val personIcon = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_my_tracker_46dp
            ) as BitmapDrawable
            locationOverlay?.setPersonIcon(personIcon.bitmap)
            mapView?.overlays?.add(locationOverlay)
            locationOverlay?.runOnFirstFix {
                requireActivity().runOnUiThread {
                    val myLocation = locationOverlay?.myLocation
                    if (myLocation != null) {
                        mapController.setCenter(GeoPoint(myLocation.latitude, myLocation.longitude))
                        mapController.setZoom(ZOOM_FOR_MY_LOCATION)
                    }
                }
            }
        } else {
            getLocationPermissions()
        }
    }

    private fun hasLocationPermissions() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun getLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
        private const val OSM_PREF = "osm_pref"
        private const val DEFAULT_ZOOM = 13.0
        private const val ZOOM_FOR_MY_LOCATION = 18.0
    }
}