package com.algapo.myratings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.algapo.myratings.DAO.AppDataBase;
import com.algapo.myratings.model.RateableThing;
import com.algapo.myratings.util.BaseGpsListener;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class NewRateableThingActivity extends AppCompatActivity implements BaseGpsListener {

    private ConstraintLayout newRateableThingConstraintLayout;
    private EditText nombreEditText;
    private Button confirmRateableThingButton;

    private String rateableThingsData;

    private LocationManager locationManager = null;
    private static final int PERMISSION_ID = 44;
    private Location location = null;
    private boolean locationActive = false;

    private AppDataBase appDataBase;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rateable_thing);

        // Obtiene elementos de la vista
        getViewElements();

        // Obtiene instancia de la base de datos
        appDataBase = AppDataBase.getAppDataBaseInstance(getApplicationContext());

        // Get extras
        rateableThingsData = getIntent().getStringExtra("rateableThingsData");

        // Inicializa locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Comienza a obtener la posición
        startLocationTracker();

    }

    private void getViewElements() {
        newRateableThingConstraintLayout = (ConstraintLayout) findViewById(R.id.newRateableThingConstraintLayout);
        nombreEditText = (EditText) findViewById(R.id.nombreEditText);
        confirmRateableThingButton = (Button) findViewById(R.id.confirmRateableThingButton);
        confirmRateableThingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!locationActive) {
                        Toast.makeText(view.getRootView().getContext(), "No teníamos preparada la posición aún, ansias\nPrueba otra vez", Toast.LENGTH_LONG).show();
                        startLocationTracker();
                    } else {
                        int trial = 0;
                        while (location == null && trial < 2000000) {
                            System.out.println("Trying to get location yet");
                            trial++;
                            if (trial % 500000 == 0) {
                                Toast.makeText(view.getContext(), "Obteniendo posición (sé paciente)", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (location == null) {
                            location = new Location("");
                            location.setLatitude(179.0);
                            location.setLongitude(0.0);
                        }
                        RateableThing rt = new RateableThing(nombreEditText.getText().toString(), location.getLatitude(), location.getLongitude());
                        Thread t = new Thread() {
                            public void run() {
                                appDataBase.rateableThingDao().insertAll(rt);
                            }
                        };
                        t.start();
                        Toast.makeText(view.getContext(), "Guadrada nueva cosa que puntuar: " + nombreEditText.getText(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "No se han podido guardar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    onBackPressed();
                }

            }
        });
    }

    private void writeDataToStorage(String filename, String data) {

        try {
            // # Para separar registros, * para separar datos del mismo registro
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(filename, Activity.MODE_PRIVATE));
            String previousData = rateableThingsData;
            data = previousData + data;
            archivo.write(data);
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
            Toast.makeText(this, "Error guardando la puntuación en el archivo " + filename, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationTracker() {
        if (checkPermissions()) {
            // check if GPS enabled
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15*1000, 1000, this);
                locationActive = true;
            } else {
                Toast.makeText(this, "El GPS debe estar habilitado.", Toast.LENGTH_LONG).show();
                requestPermissions();
            }
        }
        else
        {
            requestPermissions();
        }
    }

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    public void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        //if (!locationActive) {
        //    startLocationTracker();
        //}
    }

    public void stopLocationTracker() {
        locationManager.removeUpdates(this);
        locationActive = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Send new location to backend.
        if (location != null) {
            this.location = location;
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            Toast.makeText(this, "Última posición obtenida: Lat: " + latitude + ", Lon: " + longitude, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationTracker();
    }


}
