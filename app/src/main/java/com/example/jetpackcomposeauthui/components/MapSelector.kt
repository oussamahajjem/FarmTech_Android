package com.example.jetpackcomposeauthui.components

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapSelector(
    context: Context,
    initialPosition: GeoPoint = GeoPoint(36.8065, 10.1815), // Default to Tunisia
    onLocationSelected: (GeoPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    val mapView = remember { MapView(context) }

    DisposableEffect(mapView) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        onDispose {
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    ) { map ->
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(10.0)
        map.controller.setCenter(initialPosition)

        val marker = Marker(map)
        marker.position = initialPosition
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)

        map.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val tappedPoint = map.projection.fromPixels(event.x.toInt(), event.y.toInt())
                marker.position = tappedPoint as GeoPoint?
                onLocationSelected(tappedPoint)
                map.invalidate()
            }
            false
        }
    }
}

