package com.algapo.act2ex;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    static final String FINAL = "final";
    static final String IVA = "iva";
    static final String INICIAL = "inicial";
    static final String GUARDADO = "guardado";

    private EditText mInputCantidadInicial, mTextIvaGuardado;
    private TextView mTextCantidadFinal, mTextIva21, mTextCalcularConIvaGuardado;
    private Button mBotonIva21, mBotonIvaGuardado, mBotonCalcularConIvaGuardado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] archivos = fileList();

        mInputCantidadInicial = (EditText) findViewById(R.id.cantidadInicialEditText);
        mTextCantidadFinal = (TextView) findViewById(R.id.cantidadFinalTextView);
        mTextIva21 = (TextView) findViewById(R.id.iva21TextView);
        mTextIvaGuardado = (EditText) findViewById(R.id.ivaGuardarEditText);
        mTextCalcularConIvaGuardado = (TextView) findViewById(R.id.ivaCalcTextView);
        mBotonIva21 = (Button) findViewById(R.id.ivaButton);
        mBotonIvaGuardado = (Button) findViewById(R.id.ivaGuardarButton);
        mBotonCalcularConIvaGuardado = (Button) findViewById(R.id.calcIvaButton);

        if (savedInstanceState != null) {
            mInputCantidadInicial.setText(savedInstanceState.getString(INICIAL));
            mTextCantidadFinal.setText(savedInstanceState.getString(FINAL));
            mTextIvaGuardado.setText(savedInstanceState.getString(GUARDADO));
            mTextIva21.setText(savedInstanceState.getString(IVA));
        }

        // Calcula el IVA
        mBotonIva21.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (mInputCantidadInicial.getText().length()==0) {
                    // mostrarBrindis("ERROR Euro: Introduzca pts");
                    mostrarSnack(arg0, "ERROR: Introduzca una cantidad inicial.");
                }
                else{
                    mTextCantidadFinal.setText(String.format("%1$,.2f",Double.parseDouble(String.valueOf(mInputCantidadInicial.getText())) *1.21));
                    ocultarTeclado();
                }
            }
        });
        mBotonIvaGuardado.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                guardar_iva();
            }
        });
        mBotonCalcularConIvaGuardado.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput("iva.dat"));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    String todas = "";
                    while (linea != null) {
                        todas = todas + linea + "\n";
                        linea = br.readLine();
                    }
                    br.close();
                    archivo.close();
                    double multiplicador = 1 + (Double.parseDouble(todas)/100);
                    mTextCantidadFinal.setText(String.format("%1$,.2f",Double.parseDouble(String.valueOf(mInputCantidadInicial.getText())) * multiplicador));
                } catch (IOException e) {
                    Toast.makeText(arg0.getContext(), "Error calculando precio final con iva guardado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(INICIAL, String.valueOf(mInputCantidadInicial.getText()));
        savedInstanceState.putString(FINAL, String.valueOf(mTextCantidadFinal.getText()));
        savedInstanceState.putString(IVA, String.valueOf(mTextIva21.getText()));
        savedInstanceState.putString(GUARDADO, String.valueOf(mTextIvaGuardado.getText()));
        super.onSaveInstanceState(savedInstanceState);
    }

    private void mostrarSnack(View v, String msg) {
        Snackbar.make(v, msg, Snackbar.LENGTH_SHORT).show();
    }
    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mInputCantidadInicial.getWindowToken(), 0);
    }
    private void guardar_iva() {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput("iva.dat", Activity.MODE_PRIVATE));
            archivo.write(mTextIvaGuardado.getText().toString());
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
            Toast.makeText(this, "Error guardando iva", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "IVA guardado: " + mTextIvaGuardado.getText() + " %", Toast.LENGTH_SHORT).show();
    }
}