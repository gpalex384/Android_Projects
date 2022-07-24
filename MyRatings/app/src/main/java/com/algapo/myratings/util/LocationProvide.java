package com.algapo.myratings.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationProvide {

    private static LocationProvide instance = null;
    private static Context context;

    public static final int ONE_MINUTE = 1000 * 60;

    private static Location currentLocation;

    private LocationProvide() {

    }

    public static LocationProvide getInstance() {
        if (instance == null) {
            instance = new LocationProvide();
        }

        return instance;
    }

    public void configureIfNeeded(Context ctx) {
        if (context == null) {
            context = ctx;
            configureLocationUpdates();
        }
    }

    private void configureLocationUpdates() {
        final LocationRequest locationRequest = createLocationRequest();
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                startLocationUpdates(googleApiClient, locationRequest);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
        googleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        });

        googleApiClient.connect();
    }

    private static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(10*ONE_MINUTE);
        return locationRequest;
    }

    @SuppressLint("MissingPermission")
    private static void startLocationUpdates(GoogleApiClient client, LocationRequest request) {
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void stopLocationUpdates(Context ctx) {
        if (context == null) {
            context = ctx;
        }
        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(new LocationCallback());
    }

    @SuppressLint("MissingPermission")
    public Location getCurrentLocation() {
        return currentLocation;
    }
}
