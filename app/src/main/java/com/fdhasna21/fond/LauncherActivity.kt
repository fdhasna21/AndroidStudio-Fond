package com.fdhasna21.fond

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.fdhasna21.fond.base.BaseActivity
import com.fdhasna21.fond.databinding.ActivityLauncherBinding
import com.fdhasna21.fond.utility.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.launch

/**
 * Created by Fernanda Hasna on 26/10/2024.
 */

class LauncherActivity : BaseActivity<ActivityLauncherBinding>(ActivityLauncherBinding::inflate) {

    private var isSplashVisible = true
    private var TAG = "PERMISSION"
    private var PERMISSIONS = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    private var REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    private val REQUEST_MAP_PERMISSIONS = 99
    var fusedLocationClient: FusedLocationProviderClient? = null

    override fun setupData() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        askForPermission()
    }

    override fun setupUI() {
    }

    private fun openMainActivity() {
        lifecycleScope.launch {
            lifecycleScope.launch {
                val mainIntent = Intent(this@LauncherActivity, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
                isSplashVisible = false
            }
        }
    }

    private fun askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionX.init(this)
                .permissions(PERMISSIONS.toList())
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(
                        deniedList,
                        "Core fundamental are based on these permissions",
                        "OK",
                        "Cancel"
                    )
                }
                .request { allGranted, grantedList, deniedList ->
                    Log.d(
                        TAG,
                        "askForPermissionX: allGranted=$allGranted grantedList=$grantedList deniedList=$deniedList"
                    )
                    if (allGranted) {
                        getCurrentLocation()
                    } else {
                        askForPermission()
                    }
                }
        } else
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    if (allPermissionGranted()) {
                        getCurrentLocation()
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ")
                        if (isNeverAskChecked) {
                            Log.d(TAG, "Not Checked")
                            askForPermission()
                        } else {
                            Toast.makeText(
                                this,
                                "Please accept all the permissions before using this app",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }

                }
            }
            REQUEST_MAP_PERMISSIONS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation()
                } else {
                    Toast.makeText(
                        this,
                        "Please accept all the permissions before using this app",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun allPermissionGranted(): Boolean {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                Log.d(TAG, "allPermissionGranted except: $permission")
                return false
            }
        }
        return true
    }

    private val isNeverAskChecked: Boolean
        get() {
            for (permission in PERMISSIONS) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    return true
                }
            }
            return false
        }

    private fun getCurrentLocation() {
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
                        getCurrentLocation()
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
            fusedLocationClient?.lastLocation?.addOnCompleteListener { task : Task<Location?> ->
                if (task.isSuccessful) {
                    task.result?.let {
                        Utils.SPManager().apply {
                            saveString(this@LauncherActivity, LONGITUDE, it.longitude.toString())
                            saveString(this@LauncherActivity, LATITUDE, it.latitude.toString())
                        }
                        openMainActivity()
                    }
                }
            }
        }
    }
}