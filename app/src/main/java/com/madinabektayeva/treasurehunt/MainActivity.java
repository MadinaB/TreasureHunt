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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView compass;
    private TextView direction_field;
    private TextView steps_field;
    private CustomView customView;

    private Bitmap bitmap;
    private Bitmap pathMarkBitmap;
    private Canvas pathCanvas;
    private Resources res;
    private BitmapFactory.Options opt;
    private Paint paint;


    private boolean running = false;
    private float startStepCount;
    private float prevStepCount;
    private float lastStepCount;

    private int x;
    private int y;
    private int stepSize;
    private int stepDistance;
    private String move;

    private int degree;
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
        stepDistance = 1; //TODO() stepDistance
        currentDegree = 0f;
        degree = 0;
        move = "up";
        stepSize = 35;

        bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        pathCanvas = new Canvas();
        res = getResources();
        opt = new BitmapFactory.Options();
        opt.inMutable = true;
        pathMarkBitmap = BitmapFactory.decodeResource(res, R.drawable.pathmark, opt);
        paint = new Paint();

        x = (int) customView.getWidth() / 2;
        y = (int) customView.getHeight() / 2;

        customView.post(new Runnable() {
            @Override
            public void run() {
                x = (int) customView.getWidth() / 2;
                y = (int) customView.getHeight();
                bitmap = Bitmap.createBitmap(customView.getWidth(), customView.getHeight(), Bitmap.Config.ARGB_8888);

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
            degree = Math.round(sensorEvent.values[0]);
            direction_field.setText(Integer.toString(degree) + (char) 0x00B0);

            RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            ra.setDuration(1000);
            ra.setFillAfter(true);

            compass.startAnimation(ra);
            currentDegree = -degree;
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            lastStepCount = sensorEvent.values[0];
            if (startStepCount == -1) {
                startStepCount = sensorEvent.values[0];
                prevStepCount = startStepCount;
            }
            if (running) {
                steps_field.setText("" + String.valueOf(sensorEvent.values[0] - startStepCount));
            }

        }

        if (lastStepCount > prevStepCount + stepDistance) {
            prevStepCount = lastStepCount;
            updateNextMove();
            updatePath();
        }
    }


    public void updateNextMove() {

        if (0 < degree && degree <= 90) {
            move = "left";
        } else if (90 < degree && degree <= 180) {
            move = "up";
        } else if (180 < degree && degree <= 270) {
            move = "right";
        } else {
            move = "down";
        }

        Log.d("Log", "Move: " + move);
        Log.d("Log", "Degree: " + degree);

        if (move.equals("up")) {
            goUp();
        } else if (move.equals("down")) {
            goDown();
        } else if (move.equals("left")) {
            goLeft();
        } else if (move.equals("right")) {
            goRight();
        }

    }

    public void goRight() {
        x += stepSize;
    }

    public void goLeft() {
        x -= stepSize;
    }

    public void goUp() {
        y -= stepSize;
    }

    public void goDown() {
        y += stepSize;
    }

    public void updatePath() {
        Log.d("Log", "x: " + x);
        Log.d("Log", "y: " + y);
        pathCanvas = new Canvas(bitmap);
        pathCanvas.drawBitmap(pathMarkBitmap, x, y, paint);
        customView.drawPathMark(x, y, bitmap);
    }

    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
