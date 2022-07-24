package com.algapo.myratings;

import android.app.Activity;
import android.app.AsyncNotedAppOp;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.algapo.myratings.DAO.AppDataBase;
import com.algapo.myratings.model.RateableThing;
import com.algapo.myratings.model.Rating;
import com.algapo.myratings.util.SpinAdapter;
import com.algapo.myratings.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RequiresApi(api = Build.VERSION_CODES.O)
public class NewRatingActivity extends AppCompatActivity {

    private String rateableThingsData;
    private String ratingsData;

    private ConstraintLayout newRatingConstraintLayout;
    private Spinner cosaPuntuableSpinner;
    private SpinAdapter cosaPuntuableAdapter;
    private EditText puntuacionEditText;
    private ImageButton addRateableThingImageButton;
    private Button confirmRatingButton;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private AppDataBase appDataBase;

    private List<RateableThing> rateableThingList = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rating);

        // Obtiene elementos de la vista
        getViewElements();

        // Get extras
        ratingsData = getIntent().getStringExtra("ratingsData");

        // Obtiene instancia de la base de datos
        appDataBase = AppDataBase.getAppDataBaseInstance(getApplicationContext());

        // Lee datos del fichero
        String datosLeidosFichero = readData(Util.RATEABLE_THING_STORAGE_FILE);
        rateableThingsData = datosLeidosFichero;
        // Escribe prueba en el fichero si no existe o está vacío
        if (datosLeidosFichero == null || datosLeidosFichero.isEmpty()) {
            // idCosa, nombreCosa, posicionCosa
            String data = UUID.randomUUID().toString()+"*"
                    +"dummy"+"*"
                    +"39.0"+"*"
                    +"-1.0"+"#";
            //+"5.0"+"*"
            //+sdf.format(LocalDate.now())+"#"
            writeDataToStorage(Util.RATEABLE_THING_STORAGE_FILE, data);
            datosLeidosFichero = readData(Util.RATEABLE_THING_STORAGE_FILE);
            rateableThingsData = datosLeidosFichero;
        }
        cosaPuntuableAdapter = loadRateableThingSpinner();
        cosaPuntuableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                RateableThing rateableThing = cosaPuntuableAdapter.getItem(position);
                // Here you can do the action you want to...
                Toast.makeText(NewRatingActivity.this, "Cosa elegida: " + rateableThing.getName() +
                        "\nLat: " + rateableThing.getLatitude() + " - Lon: " + rateableThing.getLongitude(),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

    }

    private SpinAdapter loadRateableThingSpinner() {
        String datosLeidosFichero = readData(Util.RATEABLE_THING_STORAGE_FILE);
        rateableThingsData = datosLeidosFichero;
        // Lee datos de ficheros y carga el spinner
        //List<RateableThing> rateableThingsList = readFileDataRateableThings(datosLeidosFichero);
        Thread t = new Thread() {
            public void run() {
                List<RateableThing> rateableThingsList = appDataBase.rateableThingDao().getAllRateableThings();
                rateableThingList = rateableThingsList;
            }
        };
        t.start();
        SpinAdapter adapter = new SpinAdapter(
                this,  android.R.layout.simple_spinner_item, rateableThingList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cosaPuntuableSpinner.setAdapter(adapter);
        return adapter;
    }

    private List<RateableThing> readFileDataRateableThings(String datosLeidosFichero) {
        // # Para separar registros, * para separar datos del mismo registro
        // idCosa, nombreCosa, latitudCosa, longitudCosa, puntuacion, fecha
        List<RateableThing> rateableThingsList = new ArrayList<>();
        int initialLength = datosLeidosFichero.length();
        while (datosLeidosFichero.length() > 0) {
             //String ratingString = datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("#"));
            // ID cosa a puntuar
            try {
                String idCosa = datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("*"));
                datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("*") + 1);
                // Nombre cosa a puntuar
                String nombreCosa = datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("*"));
                datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("*") + 1);
                // Posicion
                double latitud = Double.parseDouble(datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("*")));
                datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("*") + 1);
                double longitud = Double.parseDouble(datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("#")));
                try {
                    datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("#") + 1);
                } catch (Exception e) {
                    System.out.println("Se ha llegado al final del fichero.");
                    datosLeidosFichero = "";
                }
                Location location = new Location("");
                location.setLatitude(latitud);
                location.setLongitude(longitud);
                /*
                // Puntuacion
                double puntuacion = Double.parseDouble(datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("*")));
                datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("*") +1);
                // Fecha
                String fechaStr = datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("#"));
                LocalDate date = LocalDate.parse(fechaStr, formatter);
                */
                RateableThing rateableThing = new RateableThing(idCosa, nombreCosa, location.getLatitude(), location.getLongitude());
                rateableThingsList.add(rateableThing);
            } catch (Exception ex) {
                datosLeidosFichero = "";
            }
        }
        return rateableThingsList;
    }

    private void writeDataToStorage(String filename, String data) {

        try {
            // # Para separar registros, * para separar datos del mismo registro
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(filename, Activity.MODE_PRIVATE));
            String previousData = ratingsData;
            data = previousData + data;
            archivo.write(data);
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
            Toast.makeText(this, "Error guardando la puntuación en el archivo " + filename, Toast.LENGTH_SHORT).show();
        }
    }

    private String readData(String filename) {
        // Ver archivos
        String[] archivos = fileList();
        if (Util.existe_archivo(archivos, filename)) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(filename));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                String todas = "";
                while (linea != null) {
                    todas = todas + linea + "\n";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
                return todas;
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private void getViewElements() {
        newRatingConstraintLayout = (ConstraintLayout) findViewById(R.id.newRatingConstraintLayout);
        puntuacionEditText = (EditText) findViewById(R.id.puntuacionEditText);
        cosaPuntuableSpinner = (Spinner) findViewById(R.id.cosaPuntuableSpinner);
        addRateableThingImageButton = (ImageButton) findViewById(R.id.addRateableThingImageButton);
        addRateableThingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent para crear una nueva cosa que puntuar
                Toast.makeText(view.getContext(), "Creando la nueva cosa que puntuar (no paras, vicios@)", Toast.LENGTH_SHORT).show();
                Intent newRateableThingIntent = new Intent(NewRatingActivity.this, NewRateableThingActivity.class);
                newRateableThingIntent.putExtra("rateableThingsData", rateableThingsData);
                startActivity(newRateableThingIntent);
            }
        });
        confirmRatingButton = (Button) findViewById(R.id.confirmRatingButton);
        confirmRatingButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                // Crea la valoración y la guarda
                try {
                    double puntuacion = Double.parseDouble(puntuacionEditText.getText().toString());
                    RateableThing rateableThing = (RateableThing) cosaPuntuableSpinner.getSelectedItem();
                    Rating r = new Rating(puntuacion, System.currentTimeMillis(), rateableThing);
                    Thread t = new Thread() {
                        public void run() {
                            appDataBase.ratingDao().insertAll(r);
                        }
                    };
                    t.start();
                    Toast.makeText(view.getContext(), "Se ha guardado! A '" + rateableThing.getName() + "' le has dado un " + puntuacion + "!!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), "No se ha podido crear la valoración: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                // y vuelve a la actividad inicial
                onBackPressed();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        int pos = 0;
        if (cosaPuntuableSpinner.getCount() > 0) {
            pos = cosaPuntuableSpinner.getSelectedItemPosition();
        }
        String datosLeidosFichero = readData(Util.RATEABLE_THING_STORAGE_FILE);
        rateableThingsData = datosLeidosFichero;
        // Lee datos de ficheros y carga el spinner
        //List<RateableThing> rateableThingsList = readFileDataRateableThings(datosLeidosFichero);
        /*Thread t = new Thread() {
            public void run() {
                List<RateableThing> rateableThingsList = appDataBase.rateableThingDao().getAllRateableThings();
                rateableThingList = rateableThingsList;
            }
        };
        t.start();

         */
        ReadRateableThingsTask task = new ReadRateableThingsTask();
        try {
            String status = task.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cosaPuntuableAdapter = new SpinAdapter(this, android.R.layout.simple_spinner_item, rateableThingList);
        cosaPuntuableSpinner.setAdapter(cosaPuntuableAdapter);
        cosaPuntuableSpinner.setSelection(pos);
        cosaPuntuableAdapter.notifyDataSetChanged();
    }

    public class ReadRateableThingsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            rateableThingList = appDataBase.rateableThingDao().getAllRateableThings();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
        }
    }
}

