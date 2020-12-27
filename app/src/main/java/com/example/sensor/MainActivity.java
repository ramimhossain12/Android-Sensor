package com.example.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity  {


     private TextView lighttextView, proxtexview ,accelTextView, gTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lighttextView = findViewById(R.id.lightSensotTextView);
        proxtexview = findViewById(R.id.ProximitySensorTextView);
        accelTextView = findViewById(R.id.accelerometertextView);
        gTextView = findViewById(R.id.grocpTextVIew);



        lighttextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LightSensorActivity.class);
                startActivity(intent);
            }
        });

        proxtexview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProximitySensorActivity.class);
                startActivity(intent);
            }
        });
        accelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AccelerometherActivity.class);
                startActivity(intent);
            }
        });

        gTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AccelerometherActivity.class);
                startActivity(intent);
            }
        });
    }


}