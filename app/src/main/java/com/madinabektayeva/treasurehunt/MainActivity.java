package com.madinabektayeva.treasurehunt;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ImageView compass;
    TextView direction_field;
    TextView steps_field;
    CustomView customView;

    Bitmap bitmap;
    Bitmap pathMarkBitmap;
    Canvas pathCanvas;
    Resources res;
    BitmapFactory.Options opt;
    Paint paint;


    boolean running = false;
    private float startStepCount;
    private float prevStepCount;
    private float lastStepCount;

    private int x;
    private int y;
    private int stepDistance;

    private float currentDegree;

    private static SensorManager sensorServiceCompass;
    private static SensorManager sensorServiceSteps;
    private Sensor compasSensor;
    private Sensor stepCountSensor;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compass = (ImageView) findViewById(R.id.compass);
        direction_field = (TextView) findViewById(R.id.direction_field);
        steps_field = (TextView) findViewById(R.id.steps_field);
        customView = (CustomView) findViewById(R.id.customView);

        sensorServiceCompass = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorServiceSteps = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compasSensor = sensorServiceCompass.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        stepCountSensor = sensorServiceSteps.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        startStepCount = -1;
        prevStepCount = 0;
        lastStepCount = 0;
        stepDistance = 1; //TODO() stepDistance = 5
        currentDegree = 0f;

        bitmap =  Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        pathCanvas = new Canvas();
        res = getResources();
        opt = new BitmapFactory.Options();
        opt.inMutable = true;
        pathMarkBitmap = BitmapFactory.decodeResource(res, R.drawable.pathmark, opt);
        paint = new Paint();

        x = (int) customView.getWidth()/2;
        y = (int) customView.getHeight()/2;
        Log.d("Log", "x: " + x);
        Log.d("Log", "y: " + y);

        customView.post(new Runnable() {
            @Override
            public void run() {
                x = (int) customView.getWidth()/2;
                y = (int) customView.getHeight();
                bitmap =  Bitmap.createBitmap(customView.getWidth(), customView.getHeight(), Bitmap.Config.ARGB_8888);
                Log.d("Log", "x: " + x);
                Log.d("Log", "y: " + y);
            }
        });

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
            //prevStepCount = finishStepCount;
            lastStepCount = sensorEvent.values[0];
            if(startStepCount == -1){
                startStepCount = sensorEvent.values[0];
                prevStepCount = startStepCount;
            }
            if (running) {
                steps_field.setText("" + String.valueOf(sensorEvent.values[0]-startStepCount));
            }
            if(lastStepCount > prevStepCount+stepDistance){
                prevStepCount = lastStepCount;
                y = y-15;
                updatePath();
            }

        }
    }

    public void updatePath(){
        pathCanvas = new Canvas(bitmap);
        pathCanvas.drawBitmap(pathMarkBitmap, x, y, paint);

        customView.drawPathMark(x, y, bitmap);
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
        //
    }


}
