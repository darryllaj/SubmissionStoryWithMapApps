package com.dicoding.submissionstoryapps.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.submissionstoryapps.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.submissionstoryapps.databinding.ActivityMapsBinding
import com.dicoding.submissionstoryapps.viewModels.ViewModelFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel by viewModels<MapsViewModels> {
        ViewModelFactory.getInstance(this)
    }

    private val boundBuilder = LatLngBounds.Builder()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        lifecycleScope.launch {
            viewModel.getAllMarkStory()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()
        setMapNightStyle()

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true


        viewModel.listStory.observe(this){ it ->
            it.forEach { it ->
                val latLng = LatLng(it.lat!!, it.lon!!)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(it.name)
                        .snippet(it.description)

                )
                boundBuilder.include(latLng)
            }
            val bounds: LatLngBounds = boundBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    500
                )
            )
        }
    }
    private val requestPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            isGranted: Boolean ->
            if (isGranted){
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private fun setMapNightStyle(){
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.mapsnight_style))
            if (!success){
                Log.e("Maps Activity", "Style Parsing Failed")
            }
        } catch (exception: Resources.NotFoundException){
            Log.e("Maps Activity", "Can't fint Style. Error: ", exception)
        }
    }

}