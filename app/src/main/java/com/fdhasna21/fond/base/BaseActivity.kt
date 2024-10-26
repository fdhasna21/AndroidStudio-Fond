package com.fdhasna21.fond.base

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewbinding.ViewBinding
import com.fdhasna21.fond.DetailActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.permissionx.guolindev.PermissionX


/**
 * Created by Fernanda Hasna on 26/10/2024.
 */

abstract class BaseActivity<VB : ViewBinding>(
    private val inflate: (LayoutInflater) -> VB
) : AppCompatActivity() {
    lateinit var binding: VB

    private var fusedLocationClient: FusedLocationProviderClient? = null
    var currentLongitude : Double = 0.0
     var currentLatitude : Double = 0.0
    private val REQUEST_MAP_PERMISSIONS = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupData()
        setupUI()
    }

    abstract fun setupData()
    abstract fun setupUI()

    fun askMapsPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissionArray = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissionArray.plus(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                PermissionX.init(this)
                    .permissions(permissionArray.toList())
                    .onExplainRequestReason{ scope, deniedList ->
                        scope.showRequestReasonDialog(
                            deniedList,
                            "Core fundamental are based on these permissions",
                            "OK",
                            "Cancel"
                        )
                    }
                    .request { allGranted, grantedList, deniedList ->
                        askMapsPermissions()
                    }
            }
            else
                ActivityCompat.requestPermissions(
                    this,
                    permissionArray,
                    REQUEST_MAP_PERMISSIONS
                )
                return
        }
        else {
            fusedLocationClient?.lastLocation?.addOnCompleteListener { task: Task<Location?> ->
                if (task.isSuccessful) {
                    task.result?.let {
                        currentLatitude = it.latitude
                        currentLongitude = it.longitude
                    }
                }
            }
        }
    }
}