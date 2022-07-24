package com.algapo.myratings.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.PowerManager;
import android.widget.Toast;

public class LocationTracker extends BroadcastReceiver {
    private PowerManager.WakeLock wakeLock;
    String servidor = "";
    String idUsuario = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pow = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //wakeLock = pow.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "cbdriver:mywakelocktag");
        wakeLock = pow.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "cbdriver:mywakelocktag");
        wakeLock.acquire();

        Location currentLocation = LocationProvide.getInstance().getCurrentLocation();
        // Send new location to backend.
        if (currentLocation != null) {
            String latitude = String.valueOf(currentLocation.getLatitude());
            String longitude = String.valueOf(currentLocation.getLongitude());
        } else {
            Toast.makeText(context, "No se ha podido obtener la posición.", Toast.LENGTH_SHORT).show();
        }

        wakeLock.release();

    }

}
