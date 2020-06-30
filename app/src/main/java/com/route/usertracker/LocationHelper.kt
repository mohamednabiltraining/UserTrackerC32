package com.route.usertracker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task

class LocationHelper{

    constructor(context: Context){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    }

    lateinit var locationRequest: LocationRequest
    private var fusedLocationClient: FusedLocationProviderClient

    interface OnCheckLocationSettingsSuccess{
        fun onSuccess();
    }

    fun checkSettingsForLocation(context: Activity,
                                 onResult: OnCheckLocationSettingsSuccess,
                                 CHECK_LOCATION_SETTINGS_REQUEST_CODE:Int){

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())


        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            onResult.onSuccess()

        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(context,
                        CHECK_LOCATION_SETTINGS_REQUEST_CODE)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }



    }

    @SuppressLint("MissingPermission")
     fun startLocationUpdates(locationCallback:LocationCallback) {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    @SuppressLint("MissingPermission")
    fun getLocation(onSuccessListener: OnSuccessListener<Location?>){
        fusedLocationClient.lastLocation.addOnSuccessListener(onSuccessListener);

    }
    fun stopUpdates(locationCallback: LocationCallback){
        fusedLocationClient.removeLocationUpdates(locationCallback)

    }
}