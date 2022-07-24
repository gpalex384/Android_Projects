package com.algapo.hellothere2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HelloThereActivity extends AppCompatActivity {

    private TextView mtexto;
    private Button mpulsador;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hellothere);

        mtexto = (TextView) findViewById(R.id.texto);
        mpulsador = (Button) findViewById(R.id.pulsador);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        mpulsador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mtexto.getText() == getString(R.string.texto2_textview)) {
                    mtexto.setText(R.string.texto1_textview);
                } else {
                    mtexto.setText(R.string.texto2_textview);
                }

                if (!vibrator.hasVibrator()) {
                    Toast.makeText(HelloThereActivity.this, "No vibrador", Toast.LENGTH_LONG).show();
                } else {
                    vibrator.vibrate(500);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast toast = Toast.makeText(getApplicationContext(),"Hola",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,200);
        toast.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast toast = Toast.makeText(getApplicationContext(),"Adiós",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL,0,200);
        toast.show();

    }
}