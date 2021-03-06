package com.example.sensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class AccelerometherActivity extends AppCompatActivity implements  SensorEventListener {

    //Sensor related
    private SensorManager sensorManager;     // Sensor manager
    private Sensor accelerometer;
    private Sensor gyro;

    private int counter=1;

    private boolean recording = false;
    private boolean counterOn = false;

    private float accValues[] = new float[3];
    private float gyroValues[] = new float[3];
    private float magValues[] = new float[3];

    private Context context;

    private static final int REQUESTCODE_STORAGE_PERMISSION = 1;

    Collection<String[]> accerelometerData = new ArrayList<>();
    Collection<String[]> gyroData = new ArrayList<>();

    private CsvWriter csvWriter=null;

    TextView stateText;
    EditText fileIDEdit;
    Switch counterSwitch;

    TextView accText;
    TextView gyroText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleromether);



        findViewById(R.id.button).setOnClickListener(listenerStartButton);
        findViewById(R.id.button2).setOnClickListener(listenerStopButton);

        fileIDEdit = (EditText) findViewById(R.id.editText);
        accText = (TextView) findViewById(R.id.textView5);
        gyroText = (TextView) findViewById(R.id.textView6);


        stateText = (TextView) findViewById(R.id.textView);
        stateText.setText("Stand by");

        counterSwitch = (Switch) findViewById(R.id.switch3);
        counterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    if (!recording) {
                        counterOn = true;
                    } else {
                        Toast.makeText(AccelerometherActivity.this, "Cannot change this option while recording.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        context=this;

        //Sensor related
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);      // Accelerometer
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);     // Step detected
    }

    private View.OnClickListener listenerStartButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            recording=true;
            stateText.setText("Recording started");
            stateText.setTextColor(Color.parseColor("#FF0000"));
        }
    };

    private int REQUEST_CODE = 1;
    private View.OnClickListener listenerStopButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording == true) {
                recording = false;

                counter=0;

                String value = fileIDEdit.getText().toString();

                stateText.setText("Recording stopped");
                stateText.setTextColor(Color.parseColor("#0000FF"));

                if (storagePermitted((Activity) context)) {
                    csvWriter = new CsvWriter();
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "accelerometer" + value + ".csv");
                    File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "gyroscope" + value + ".csv");



                }
            } else {
                Toast.makeText(AccelerometherActivity.this, "Nothing to save. Recording was not started.", Toast.LENGTH_LONG).show();
            }

        }
    };





    @Override
    protected void onResume() {
        super.onResume();

        // Sensor listeners registration
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Converts timestamp to milliseconds
        long timeInMillis = (new Date()).getTime()
                + (event.timestamp - System.nanoTime()) / 1000000L;

        if (recording) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accText.setText("Accelerometer:   X= " + roundThis(event.values[0]) + "   Y= " + roundThis(event.values[1]) + "   Z= " + roundThis(event.values[2]));
                Log.d("Record", "Accelerometer" + String.valueOf(counter));
                accValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyroText.setText("Gyroscope:   X= " + roundThis(event.values[0]) + "   Y= " + roundThis(event.values[1]) + "   Z= " + roundThis(event.values[2]));
                Log.d("Record", "Gyroscope" + String.valueOf(counter));
                gyroValues = event.values;
            }


            if (counterOn){
                accerelometerData.add(new String[]{String.valueOf(counter), String.valueOf(accValues[0]), String.valueOf(accValues[1]), String.valueOf(accValues[2])});
                gyroData.add(new String[]{String.valueOf(counter), String.valueOf(gyroValues[0]), String.valueOf(gyroValues[1]), String.valueOf(gyroValues[2])});

            } else {
                accerelometerData.add(new String[]{String.valueOf(timeInMillis), String.valueOf(accValues[0]), String.valueOf(accValues[1]), String.valueOf(accValues[2])});
                gyroData.add(new String[]{String.valueOf(timeInMillis), String.valueOf(gyroValues[0]), String.valueOf(gyroValues[1]), String.valueOf(gyroValues[2])});

            }

            counter++;
        }

    }
    private static boolean storagePermitted(Activity activity) {

        // Check read and write permissions
        Boolean readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (readPermission && writePermission) {
            return true;
        }

        // Request permission to the user
        ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUESTCODE_STORAGE_PERMISSION);

        return false;
    }

    // Rounds the value
    public static float roundThis(float value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(4, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Mandatory
    }

    private class CsvWriter {
    }
}

