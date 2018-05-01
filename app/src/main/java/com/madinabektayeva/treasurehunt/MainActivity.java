package com.madinabektayeva.treasurehunt;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ImageView compass;
    TextView direction_field;
    TextView steps_field;

    boolean running = false;
    private static float startStepCount;
    private static float finishStepCount;

    private static SensorManager sensorServiceCompass;
    private static SensorManager sensorServiceSteps;
    private Sensor compasSensor;
    private Sensor stepCountSensor;

    private float currentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compass = (ImageView) findViewById(R.id.compass);
        direction_field = (TextView) findViewById(R.id.direction_field);
        steps_field = (TextView) findViewById(R.id.steps_field);

        sensorServiceCompass = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorServiceSteps = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compasSensor = sensorServiceCompass.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        stepCountSensor = sensorServiceSteps.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        startStepCount = -1;
        finishStepCount = -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        if (compasSensor != null) {
            sensorServiceCompass.registerListener(this, compasSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(MainActivity.this, "Not supported", Toast.LENGTH_SHORT).show();
        }

        if (stepCountSensor != null) {
            sensorServiceSteps.registerListener((SensorEventListener) this, stepCountSensor, SensorManager.SENSOR_DELAY_UI);

        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }

    }

    protected void onPause() {
        super.onPause();
        sensorServiceCompass.unregisterListener(this);
        sensorServiceSteps.unregisterListener((SensorListener) this);
        running = false;
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            int degree = Math.round(sensorEvent.values[0]);
            direction_field.setText(Integer.toString(degree) + (char) 0x00B0);

            RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            ra.setDuration(1000);
            ra.setFillAfter(true);

            compass.startAnimation(ra);
            currentDegree = -degree;
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if(startStepCount == -1){
                startStepCount = sensorEvent.values[0];
            }
            if (running) {
                steps_field.setText("" + String.valueOf(sensorEvent.values[0]-startStepCount));
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
        //
    }


}
