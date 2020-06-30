package com.route.usertracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.route.notesapplicationc32.Base.BaseActivity

class MainActivity : BaseActivity() {
    lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationHelper = LocationHelper(this);
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION))
           checkSettingsForLocation()
        else

            requestPermissionFromUser(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        ACCESS_LOCATION_REQUEST_CODE,
            "please allow location permission to be able to request a new car"
            ,"ok",
            object :OnRequestPermissionResponseListener{
                override fun onRequestPermissionResponse(requestCode: Int, State: Int) {
                    if (State==PERMISSION_GRANTED){
                        checkSettingsForLocation()
                    }else {
                        Toast.makeText(this@MainActivity,"user denied permission",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        );
    }

    fun checkSettingsForLocation(){
        locationHelper.checkSettingsForLocation(this,
            object :LocationHelper.OnCheckLocationSettingsSuccess{
                override fun onSuccess() {
                    showUserLocation()
                }
            },CHECK_LOCATION_SETTINGS_REQUEST_CODE)
    }
    val locationCallback = object : LocationCallback(){

        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations){
                // Update UI with location data
                // ...
                Log.e("new Location",""+ location.latitude+" "+location.longitude)
            }

        }
    }

    val ACCESS_LOCATION_REQUEST_CODE = 300
    val CHECK_LOCATION_SETTINGS_REQUEST_CODE  =500;

    @SuppressLint("MissingPermission")
    fun showUserLocation(){

       locationHelper.getLocation(OnSuccessListener{ location: Location? ->
           Log.e("location",""+location?.latitude+" "+location?.longitude)
       })
        Toast.makeText(this,"you're in egypt",Toast.LENGTH_LONG).show()
        locationHelper.startLocationUpdates(locationCallback)
    }


    override fun onStop() {
        super.onStop()
        locationHelper.stopUpdates(locationCallback)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHECK_LOCATION_SETTINGS_REQUEST_CODE){
            if (resultCode==Activity.RESULT_OK){
                showUserLocation()
            }else if (resultCode==Activity.RESULT_CANCELED){
                showMessage(title=null,
                        message = "we can't request a new car for you")

            }
        }
    }

   }