package com.algapo.mydinero2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    static final String EURO = "euro";
    static final String DOLAR = "dolar";
    static final String LIBRA = "libra";

    private EditText mInput;
    private TextView mTextEuro, mTextDolar, mTextLibra, mTextChDolar, mTextChLibra;
    private Button mBotonEuro, mBotonDolar, mBotonLibra, mBotonPort, mBotonChDolar, mBotonChLibra;
    private ProgressBar mBar;
    private ImageView mImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] archivos = fileList();

        mInput = (EditText) findViewById(R.id.editDinero);
        mTextEuro = (TextView) findViewById(R.id.textEuro);
        mTextDolar = (TextView) findViewById(R.id.textDolar);
        mTextLibra = (TextView) findViewById(R.id.textLibra);
        mTextChDolar = (TextView) findViewById(R.id.textChDolar);
        mTextChLibra = (TextView) findViewById(R.id.textChLibra);
        mBotonEuro = (Button) findViewById(R.id.botonEuro);
        mBotonDolar = (Button) findViewById(R.id.botonDolar);
        mBotonLibra = (Button) findViewById(R.id.botonLibra);
        mBotonPort = (Button) findViewById(R.id.botonPort);
        mBotonChDolar = (Button) findViewById(R.id.botonChDolar);
        mBotonChLibra = (Button) findViewById(R.id.botonChLibra);
        // mBar = (ProgressBar) findViewById(R.id.barra);
        mImagen = (ImageView) findViewById(R.id.imagen);

        if (existe_archivo(archivos, "dolar.dat")) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput("dolar.dat"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                String todas = "";
                while (linea != null) {
                    todas = todas + linea + "\n";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
                mTextChDolar.setText(todas);
            } catch (IOException e) {
                Toast.makeText(this, "Error leyendo dólar", Toast.LENGTH_SHORT).show();
            }
        }

        if (existe_archivo(archivos, "libra.dat")) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput("libra.dat"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                String todas = "";
                while (linea != null) {
                    todas = todas + linea + "\n";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
                mTextChLibra.setText(todas);
            } catch (IOException e) {
                Toast.makeText(this, "Error leyendo dólar", Toast.LENGTH_SHORT).show();
            }
        }

        if (savedInstanceState != null) {
            mTextEuro.setText(savedInstanceState.getString(EURO));
            mTextDolar.setText(savedInstanceState.getString(DOLAR));
            mTextLibra.setText(savedInstanceState.getString(LIBRA));
        }

        mBotonEuro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (mInput.getText().length()==0) {
                    // mostrarBrindis("ERROR Euro: Introduzca pts");
                    mostrarSnack(arg0, "ERROR Euro: Introduzca pts");
                }
                else{
                    mTextEuro.setText(String.format("%1$,.2f",Double.parseDouble(String.valueOf(mInput.getText())) / 166.386) + "€");
                    ocultarTeclado();
                }
            }
        });
        mBotonDolar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (mInput.getText().length()==0) {
                    // mostrarBrindis("ERROR Dolar: Introduzca pts");
                    mostrarSnack(arg0, "ERROR Dolar: Introduzca pts");
                }
                else{
                    mTextDolar.setText(String.format("%1$,.2f",Double.parseDouble(String.valueOf(mInput.getText())) /
                            166.386 / Double.parseDouble(String.valueOf(mTextChDolar.getText()))) + "$");
                    ocultarTeclado();
                }
            }
        });
        mBotonLibra.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (mInput.getText().length()==0) {
                    // mostrarBrindis("ERROR Libra: Introduzca pts");
                    mostrarSnack(arg0,"ERROR Libra: Introduzca pts");
                }
                else{
                    mTextLibra.setText(String.format("%1$,.2f",Double.parseDouble(String.valueOf(mInput.getText())) /
                            166.386 / Double.parseDouble(String.valueOf(mTextChLibra.getText()))) + "£");
                    ocultarTeclado();
                }
            }
        });
        mBotonPort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Fijamos el layout en vertical
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
        mBotonChDolar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar_dolar();
            }
        });
        mBotonChLibra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar_libra();
            }
        });
        mImagen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                mInput.setText("");
                mTextEuro.setText("");
                mTextDolar.setText("");
                mTextLibra.setText("");
                mBar.setVisibility(View.INVISIBLE);
                ocultarTeclado();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }
        });
    }//Fin de onCreate
    private void mostrarSnack(View v, String msg) {
        ocultarTeclado();
//        Toast toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER,  0, 0);
//        toast.show();
        Snackbar.make(v, msg, Snackbar.LENGTH_SHORT).show();

    }
    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(EURO, String.valueOf(mTextEuro.getText()));
        savedInstanceState.putString(DOLAR, String.valueOf(mTextDolar.getText()));
        savedInstanceState.putString(LIBRA, String.valueOf(mTextLibra.getText()));
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mimenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Toast.makeText(getApplicationContext(), "¿Quieres cerrar la aplicación?", Toast.LENGTH_SHORT).show();
        finishAffinity();
        return true;
    }

    private void guardar_dolar() {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("dolar.dat", Activity.MODE_PRIVATE));
            archivo.write(mTextChDolar.getText().toString());
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
            Toast.makeText(this, "Error guardando dólar", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Dólar guardado", Toast.LENGTH_SHORT).show();
    }

    private void guardar_libra() {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("libra.dat", Activity.MODE_PRIVATE));
            archivo.write(mTextChLibra.getText().toString());
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
            Toast.makeText(this, "Error guardando libra", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Libra guardado", Toast.LENGTH_SHORT).show();
    }

    private boolean existe_archivo(String[] archivos, String archbusca) {
        for (int f = 0; f < archivos.length; f++)
            if (archbusca.equals(archivos[f]))
                return true;
        return false;
    }

}