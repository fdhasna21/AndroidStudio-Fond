package com.fdhasna21.fond

import android.graphics.Color
import android.graphics.Color.alpha
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.fdhasna21.fond.adapter.ItemGalleryAdapter
import com.fdhasna21.fond.base.BaseActivity
import com.fdhasna21.fond.databinding.ActivityDetailBinding
import com.fdhasna21.fond.model.ItemGallery
import com.fdhasna21.fond.model.response.ItemResto
import com.fdhasna21.fond.utility.Utils
import com.fdhasna21.fond.utility.network.ServerAPI
import com.fdhasna21.fond.utility.network.ServerInterface
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Fernanda Hasna on 26/10/2024.
 * Updated by Fernanda Hasna on 27/10/2024.
 */

class DetailActivity : BaseActivity<ActivityDetailBinding>(ActivityDetailBinding::inflate),
    OnClickListener, SwipeRefreshLayout.OnRefreshListener, OnMapReadyCallback, OnCameraIdleListener, OnCameraMoveListener, OnMarkerClickListener {

    private var galleryList = arrayListOf<ItemGallery>()
    private lateinit var itemAdapter: ItemGalleryAdapter
    private lateinit var layoutManager : GridLayoutManager

    private lateinit var resto : ItemResto
    private lateinit var mMap: GoogleMap

    override fun setupData() {
        resto = intent.getParcelableExtra<ItemResto>(Utils.INTENT.DETAIL) ?: ItemResto()
        getData()
    }

    override fun setupUI() {
        binding.apply {
            /**== Setup Action Bar ==**/
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

                /**== Setup Swipe Refresh ==**/
                refreshRecyclerView.setOnRefreshListener(this@DetailActivity)

                /**== Setup Recycler View ==**/
                itemAdapter = ItemGalleryAdapter( this@DetailActivity, galleryList)
                layoutManager = GridLayoutManager(this@DetailActivity, 3)

                recyclerView.apply {
                    isNestedScrollingEnabled = false
                    layoutManager = this@DetailActivity.layoutManager
                    adapter = itemAdapter
                    addItemDecoration(object : DividerItemDecoration(this@DetailActivity, VERTICAL) {})
                    setHasFixedSize(true)
                    itemAdapter.notifyDataSetChanged()
                }

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

    override fun onRefresh() {
        binding.refreshRecyclerView.isRefreshing = false
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
                    val latlongCurr = getCurrentLocation()
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

    private fun getCurrentLocation() : LatLng {
        Utils.SPManager().apply {
            val long = Utils.SPManager().getString(this@DetailActivity, LONGITUDE, (0.0).toString()).toDouble()
            val lat = Utils.SPManager().getString(this@DetailActivity, LATITUDE, (0.0).toString()).toDouble()
            return LatLng(lat, long)
        }
    }

    private fun getData(){
        resto.fsqId?.let {
            ServerAPI().getServerAPI(this)
                .create(ServerInterface::class.java)
                .getGallery(
                    fsqId = it
                ).enqueue(object : Callback<ArrayList<ItemGallery>> {
                    override fun onResponse(
                        call: Call<ArrayList<ItemGallery>>,
                        response: Response<ArrayList<ItemGallery>>
                    ) {
                        binding.refreshRecyclerView.isRefreshing = false
                        if (response.isSuccessful) {
                            response.body().let {
                                if (it != null) {
                                    galleryList.clear()
                                    galleryList.addAll(it)
                                    itemAdapter.notifyDataSetChanged()
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@DetailActivity,
                                "${response.code()} : ${response.body()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ArrayList<ItemGallery>>, t: Throwable) {
                        binding.refreshRecyclerView.isRefreshing = false
                        Toast.makeText(
                            this@DetailActivity,
                            "Failed to connect to the server.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}