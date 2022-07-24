package com.algapo.myintent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class HubActivity extends AppCompatActivity {

    static final int RESPUESTA_FOTO = 1; //al principio de la clase

    private EditText mMandarEditText;
    private ImageButton mNavegarButton;
    private ImageButton mAlarmaButton;
    private ImageButton mSmsButton;
    private ImageButton mFotoButton;
    private ImageButton mLlamadaButton;
    private ImageButton mMapaButton;
    private ImageButton mMandarButton;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        mMandarEditText = (EditText) findViewById(R.id.mandarEditText);

        mNavegarButton = (ImageButton) findViewById(R.id.navegarButton);
        mAlarmaButton = (ImageButton) findViewById(R.id.alarmaButton);
        mSmsButton = (ImageButton) findViewById(R.id.smsButton);
        mFotoButton = (ImageButton) findViewById(R.id.fotoButton);
        mLlamadaButton = (ImageButton) findViewById(R.id.llamadaButton);
        mMapaButton = (ImageButton) findViewById(R.id.mapaButton);
        mMandarButton = (ImageButton) findViewById(R.id.mandarButton);
        mTextView = (TextView) findViewById(R.id.nombreTextView);

        Bundle extras = getIntent().getExtras();
        String nombre = extras.getString("nombre");
        mTextView.setText("Hola " + nombre);

        mNavegarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent navegaIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.uji.es"));
                if (navegaIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(navegaIntent);
                }
            }
        });
        mAlarmaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent alarmaIntent = new Intent(AlarmClock.ACTION_SET_ALARM)
                        .putExtra(AlarmClock.EXTRA_MESSAGE, "message")
                        .putExtra(AlarmClock.EXTRA_HOUR, 0)
                        .putExtra(AlarmClock.EXTRA_MINUTES, 45);
                if (alarmaIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(alarmaIntent);
                }
            }
        });
        mSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + 609000000));
                smsIntent.putExtra("sms_body", "Texto  mensaje");
                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                }
            }
        });
        mFotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HubActivity.this, FotoActivity.class);
                startActivityForResult(i, RESPUESTA_FOTO);
            }
        });
        mLlamadaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent llamaIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + 963000000));
                    startActivity(llamaIntent);
                } catch (Exception e) {
                    //gestionar excepción o dar notificación
                }
            }
        });
        mMapaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:40.0, 0.0");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        mMandarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] TO = {"emailde@destino.com"};
                String[] CC = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/html"); //text/plain
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Escribe aquí su asunto");
                emailIntent.putExtra(Intent.EXTRA_TEXT, mMandarEditText.getText());
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Enviar..."));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String resultado = data.getExtras().getString("RESPUESTA_FOTO");
            mTextView.setText(resultado); //La respuesta la veremos en el TextView
        }else{
            Toast.makeText(HubActivity.this, "No hubo respuesta", Toast.LENGTH_SHORT).show();
        }
    }
}