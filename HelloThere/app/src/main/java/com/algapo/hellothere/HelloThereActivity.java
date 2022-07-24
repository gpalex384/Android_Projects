package com.algapo.hellothere;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelloThereActivity extends AppCompatActivity {

    TextView mtexto;
    Button mpulsador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hellothere);

        mtexto = (TextView) findViewById(R.id.texto);
        mpulsador = (Button) findViewById(R.id.pulsador);

        mpulsador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mtexto.getText() == "Bye!") {
                    mtexto.setText("Hello There!");
                } else {
                    mtexto.setText("Bye!");
                }
            }
        });
    }
}