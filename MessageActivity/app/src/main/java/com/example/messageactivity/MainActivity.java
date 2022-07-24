package com.example.messageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    private EditText txtNombre;
    private Button btnAceptar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Obtenemos una referencia a los controles de la interfaz
        txtNombre = (EditText)findViewById(R.id.txtNombre);
        btnAceptar = (Button)findViewById(R.id.btnAceptar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                //Creamos la información a pasar entre actividades
                intent.putExtra("NOMBRE", txtNombre.getText().toString());
                //Iniciamos la nueva actividad
                startActivity(intent);
            }
        });
    }
}