package com.algapo.myratings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.algapo.myratings.DAO.AppDataBase;
import com.algapo.myratings.model.RateableThing;
import com.algapo.myratings.model.Rating;
import com.algapo.myratings.util.BaseGpsListener;
import com.algapo.myratings.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private DateTimeFormatter normalFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ConstraintLayout mainConstraintLayout;
    private ScrollView mainScrollView;
    private LinearLayout innerScrollLinearLayout;
    private LinearLayout noRatingsLinearLayout;
    private TextView voidMessageTextView;
    private Button addRatingButton;

    private String ratingsData;

    private List<Rating> ratingList;

    private RateableThing rateableThing;

    AppDataBase appDataBase;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtiene instancia de la base de datos
        appDataBase = AppDataBase.getAppDataBaseInstance(getApplicationContext());

        // Obtiene elementos de la vista
        getViewElements();

        // Comprueba si hay datos de valoraciones y las pinta en pantalla
        tryDisplayRatings();

    }

    private void getViewElements() {
        mainConstraintLayout = (ConstraintLayout) findViewById(R.id.mainConstraintLayout);
        mainScrollView = (ScrollView) findViewById(R.id.mainScrollView);
        innerScrollLinearLayout = (LinearLayout) findViewById(R.id.innerScrollLinearLayout);
        voidMessageTextView = (TextView) findViewById(R.id.voidMessageTextView);
        addRatingButton = (Button) findViewById(R.id.addRatingButton);
        addRatingButton.setOnClickListener(view -> {
            Intent newRatingIntent = new Intent(MainActivity.this, NewRatingActivity.class);
            newRatingIntent.putExtra("ratingsData", ratingsData);
            startActivity(newRatingIntent);
        });
    }
    private void tryDisplayRatings() {
        // Comprueba si hay datos de valoraciones
        String ratingsDataStr = readData(Util.RATING_STORAGE_FILE);
        ratingsData = ratingsDataStr;

        ReadRatingsTask task = new ReadRatingsTask();
        try {
            task.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (ratingList == null || ratingList.isEmpty()) {
            String msg = "No hay puntuaciones que mostrar";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            this.voidMessageTextView.setText(msg);
        } else {
            System.out.println("Obteniendo valoraciones...");
            System.out.println("Pintando las valoraciones en pantalla. Total: " + ratingList.size() + " valoraciones.");
            displayRatings(ratingList);
        }

        /*
        // Si no hay datos de valoraciones, muestra un mensaje
        if (ratingsData == null || ratingsData.isEmpty()) {
            String msg = "No hay puntuaciones que mostrar";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            this.voidMessageTextView.setText(msg);
        }
        // Si hay datos de valoraciones, enséñalos
        else {
            System.out.println("Obteniendo valoraciones...");
            List<Rating> ratingList = readFileDataRateableThings(ratingsData);
            System.out.println("Pintando las valoraciones en pantalla. Total: " + ratingList.size() + " valoraciones.");
            displayRatings(ratingList);
        }

         */
    }

    private void displayRatings(List<Rating> ratingList) {
        // Elimina vistas previas en el scrollview
        innerScrollLinearLayout.removeAllViews();

        // Recorre las puntuaciones y las muestra como elementos dentro de un layout horizontal
        for (Rating rating : ratingList) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);            params.setMargins(10,10,10,10);
            params.setMargins(10, 0, 10, 0);
            // Layout para el rating (vertical)
            LinearLayout ratingLayout = new LinearLayout(this);
            ratingLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ratingLayout.setOrientation(LinearLayout.VERTICAL);
            ratingLayout.setGravity(Gravity.TOP);
            ratingLayout.setPadding(5, 10, 5, 5);
            ratingLayout.setTag(rating);
            ratingLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteRating(view);
                }
            });

            // Linear layout para el título
            LinearLayout titleLayout = new LinearLayout(this);
            titleLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            titleLayout.setOrientation(LinearLayout.HORIZONTAL);
            titleLayout.setGravity(Gravity.TOP);
            titleLayout.setPadding(5, 10, 5, 5);

            // Textview del título y lo añade al layout de título
            TextView titleTextView = new TextView(this);
            String rateableThingId = rating.getRateableThingId();
            ReadRateableThingByIdTask task = new ReadRateableThingByIdTask();
            try {
                task.execute(rateableThingId).get();    // rellena la variable rateableThing a partir del id
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            titleTextView.setText(rateableThing.getName());
            titleTextView.setGravity(Gravity.CENTER);
            titleLayout.addView(titleTextView);

            // Linear layout para los datos
            LinearLayout ratingDataLayout = new LinearLayout(this);
            ratingDataLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ratingDataLayout.setOrientation(LinearLayout.HORIZONTAL);
            ratingDataLayout.setGravity(Gravity.TOP);
            ratingDataLayout.setPadding(5, 5, 5, 10);

            // Textview de la fecha y lo añade al layout de datos
            TextView dateTextView = new TextView(this);
            LocalDate ratingDate =
                    Instant.ofEpochMilli(rating.getRatingDate()).atZone(ZoneId.systemDefault()).toLocalDate();
            dateTextView.setText(ratingDate.format(normalFormatter));
            dateTextView.setGravity(Gravity.CENTER);
            dateTextView.setLayoutParams(params);
            ratingDataLayout.addView(dateTextView);

            // Textview de la puntuación y lo añade al layout de datos
            TextView ratingTextView = new TextView(this);
            ratingTextView.setText(String.valueOf(rating.getRate()));
            ratingTextView.setGravity(Gravity.CENTER);
            ratingTextView.setWidth(convertDpToPx(80));
            ratingTextView.setLayoutParams(params);
            ratingDataLayout.addView(ratingTextView);

            // Añade los dos layouts horizontales al layout del rating
            ratingLayout.addView(titleLayout);
            ratingLayout.addView(ratingDataLayout);

            // Añade el layout del rating al layout vertical general
            innerScrollLinearLayout.addView(ratingLayout);

        }
    }

    private void deleteRating(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea eliminar la puntuación seleccionada?");
        builder.setTitle("Eliminar puntuación");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Thread t = new Thread() {
                    public void run() {
                        appDataBase.ratingDao().delete((Rating) view.getTag());
                    }
                };
                t.start();
                Toast.makeText(view.getContext(), "Puntuación eliminada (porque así lo has querido, que por mí ahí quedaba)", Toast.LENGTH_LONG).show();
                tryDisplayRatings();
            }
        });
        builder.setNegativeButton("NEIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Toast.makeText(view.getContext(), "Te has rajao eh, mequetrefe", Toast.LENGTH_SHORT).show();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String readData(String filename) {
        // Ver archivos
        System.out.println("Leyendo el archivo '" + filename + "'...");
        String[] archivos = fileList();
        if (Util.existe_archivo(archivos, filename)) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(filename));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                String todas = "";
                while (linea != null) {
                    todas = todas + linea;
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
                System.out.println("Leído!");
                return todas;
            } catch (IOException e) {
                System.out.println("Error leyendo el archivo '" + filename + "': " + e.getMessage());
                return null;
            }
        } else {
            System.out.println("No existe el archivo '" + filename + "'");
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Rating> readFileDataRateableThings(String datosLeidosFichero) {
        // # Para separar registros, * para separar datos del mismo registro
        // idRating, idCosa, nombreCosa, latitudCosa, longitudCosa, puntuacion, fecha
        List<Rating> ratingsList = new ArrayList<>();
        int initialLength = datosLeidosFichero.length();
        while (datosLeidosFichero.length() > 0) {
            try {
                // ID Rating
                String idRating = datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("*"));
                datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("*") + 1);
                // ID cosa a puntuar
                String idCosa = datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("*"));
                datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("*") + 1);
                // Nombre cosa a puntuar
                String nombreCosa = datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("*"));
                datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("*") + 1);
                // Posicion
                double latitud = Double.parseDouble(datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("*")));
                datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("*") + 1);
                double longitud = Double.parseDouble(datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("*")));
                datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("*") + 1);
                Location location = new Location("");
                location.setLatitude(latitud);
                location.setLongitude(longitud);
                // Puntuacion
                double puntuacion = Double.parseDouble(datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("*")));
                datosLeidosFichero = datosLeidosFichero.substring(datosLeidosFichero.indexOf("*") +1);
                // Fecha
                String fechaStr = datosLeidosFichero.substring(0, datosLeidosFichero.indexOf("#"));
                LocalDate date = LocalDate.parse(fechaStr, formatter);

                RateableThing rateableThing = new RateableThing(idCosa, nombreCosa, location.getLatitude(), location.getLongitude());
                Rating rating = new Rating(idRating, puntuacion, Timestamp.valueOf(String.valueOf(date.atStartOfDay())).getTime(), rateableThing);
                ratingsList.add(rating);
            } catch (Exception ex) {
                System.out.println("Ha habido un error transformando los datos de valoraciones en objetos valoración.");
                datosLeidosFichero = "";
            }
        }
        return ratingsList;
    }

    private int convertDpToPx(float dp){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int)((int)dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        tryDisplayRatings();
    }

    public class ReadRatingsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            ratingList = appDataBase.ratingDao().getAllRatings();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
        }
    }
    public class ReadRateableThingByIdTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            List<RateableThing> rtList = appDataBase.rateableThingDao().getAllRateableThingsByIds(params);
            if (!rtList.isEmpty()) {
                rateableThing = rtList.get(0);
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
        }
    }

}