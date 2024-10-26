package com.fdhasna21.fond

import android.graphics.Color
import android.graphics.Color.alpha
import android.view.View
import android.view.View.OnClickListener
import com.bumptech.glide.Glide
import com.fdhasna21.fond.base.BaseActivity
import com.fdhasna21.fond.databinding.ActivityDetailBinding
import com.fdhasna21.fond.model.response.ItemResto
import com.fdhasna21.fond.utility.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

/**
 * Created by Fernanda Hasna on 26/10/2024.
 */

class DetailActivity : BaseActivity<ActivityDetailBinding>(ActivityDetailBinding::inflate), OnClickListener,
    OnMapReadyCallback, OnCameraIdleListener, OnCameraMoveListener, OnMarkerClickListener {

    private lateinit var resto : ItemResto
    private lateinit var mMap: GoogleMap

    override fun setupData() {
        resto = intent.getParcelableExtra<ItemResto>(Utils.INTENT.DETAIL) ?: ItemResto()
        askMapsPermissions()
    }

    override fun setupUI() {
        binding.apply {
            setSupportActionBar(topAppBar)
            supportActionBar?.apply {
                setHomeAsUpIndicator(R.drawable.ic_left);
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(false)
            }

            resto.apply {
                /**== Setup Button Listener ==**/
                addressMore.setOnClickListener(this@DetailActivity)
                galleryMore.setOnClickListener(this@DetailActivity)

                /**== Setup Mapping Data ==**/
                detailName.text = name
                detailDistance.text = distance.toString() + " m"

                location?.let{ loc ->
                    val fullAddress = loc.address + if(loc.crossStreet != null && loc.crossStreet != "" && loc.crossStreet != "null"){
                        "(" + loc.crossStreet + ")"
                    } else {
                        ""
                    }
                    address.text = fullAddress
                    region.text = loc.region
                    val fullCountry  = if(loc.postcode != null && loc.postcode != "" && loc.postcode != "null"){
                        loc.postcode + ", " + loc.country
                    } else {
                        loc.country
                    }
                    country.text = fullCountry
                }

                categories?.get(0)?.let { category ->
                    detailCategory.text = category.shortName
                    category.icon?.let {
                        (it.prefix != null && it.suffix != null).apply {
                            Glide.with(this@DetailActivity)
                                .load(it.prefix + "64" + it.suffix)
                                .circleCrop()
                                .into(rowImage)
                        }
                    }
                }
            }
        }

        /**== Setup Maps ==**/
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onClick(v: View?) {
        binding.apply {
            when (v?.id) {
                addressMore.id -> {
                    if(layoutAddress.visibility == View.VISIBLE){
                        layoutAddress.visibility = View.GONE
                        addressMore.setImageResource(R.drawable.ic_expand)
                    } else {
                        layoutAddress.visibility = View.VISIBLE
                        addressMore.setImageResource(R.drawable.ic_collapse)
                    }
                }

                galleryMore.id -> {
                    if(recyclerView.visibility == View.VISIBLE){
                        recyclerView.visibility = View.GONE
                        galleryMore.setImageResource(R.drawable.ic_expand)
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        galleryMore.setImageResource(R.drawable.ic_collapse)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.setOnCameraMoveListener(this)
        mMap.setOnCameraIdleListener(this)
        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
        setMarkLocation()
    }

    override fun onCameraIdle() {}
    override fun onCameraMove() {}
    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    fun setMarkLocation() {
        resto.geocodes?.main?.let {
            it.latitude?.toDouble()
                ?.let { it1 -> it.longitude?.toDouble()?.let { it2 ->
                    val latlongCurr = LatLng(currentLatitude, currentLongitude)
                    val currMark = MarkerOptions().position(latlongCurr)
                    currMark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    currMark.draggable(false)
                    currMark.title("Current Location")
                    mMap.addMarker(currMark)?.showInfoWindow()

                    val latlong = LatLng(it1, it2)
                    val restoMark = MarkerOptions().position(latlong)
                    restoMark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    restoMark.draggable(false)
                    restoMark.title("Destination")
                    mMap.addMarker(restoMark)?.showInfoWindow()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 13.5f))

                    val line = PolylineOptions().apply {
                        addAll(arrayListOf(latlong, latlongCurr))
                        geodesic(true)
                        color(Color.BLUE)
                        alpha(1)
                        width(2f)
                    }
                    mMap.addPolyline(line)
                } } }
    }
}