package com.bluebloodapps.device_test.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bluebloodapps.device_test.MainActivity;
import com.bluebloodapps.device_test.R;
import com.bluebloodapps.device_test.utils.Status;
import com.bluebloodapps.device_test.utils.TestType;

import java.io.IOException;

public class ProximityCheck extends AppCompatActivity implements SensorEventListener{
    boolean timerRunning = false;
    private CountDownTimer timer;

    TextView instructionText;

    Button continueBtn, startBtn;

    boolean isClose, testRunning;
    float maxRange;

    public static MainActivity.TestCallback callback;
    public static MainActivity mainActivity;

    private SensorManager mSensorManager;
    private Sensor mProximity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);

        instructionText = findViewById(R.id.instructionText);

        continueBtn = findViewById(R.id.continueBtn);
        startBtn =  findViewById(R.id.startBtn);

        startBtn.setOnClickListener(view -> {
            testRunning = true;
            instructionText.setText("Acercá el telefono a tu oreja hasta escuchar el sonido.");
            startBtn.setVisibility(View.GONE);
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        maxRange = mProximity.getMaximumRange()*10;
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {
        if(!testRunning) return;

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= maxRange/3) {
                isClose = false;
                if(timerRunning){
                    timer.cancel();
                    timerRunning = false;
                    testRunning = false;
                    instructionText.setText("Prueba fallida");
                    startBtn.setVisibility(View.VISIBLE);
                }
            } else {
                isClose = true;
                if(!timerRunning){
                    timer = new CountDownTimer(3000, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            if(isClose){
                                instructionText.setText("¡Prueba finalizada!");

                                MediaPlayer mp1 = new MediaPlayer();
                                try {
                                    mp1.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                                    mp1.setDataSource(ProximityCheck.this, Uri.parse("android.resource://com.bluebloodapps.device_test/" + R.raw.uno));
                                    mp1.prepare();
                                    mp1.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                continueBtn.setOnClickListener(view -> {
                                    finish();
                                    Status status = new Status("Funcionando", R.color.green, true);
                                    mainActivity.updateTestStatus(TestType.PROXIMITY, status);
                                    callback.onSuccess();
                                });

                                continueBtn.setVisibility(View.VISIBLE);
                            }else{
                                instructionText.setText("Prueba fallida");
                            }
                            timerRunning = false;
                            testRunning = false;
                        }
                    };
                    timer.start();
                    timerRunning = true;
                }
            }
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static MainActivity.TestCallback getCallback() {
        return callback;
    }

    public static void setCallback(MainActivity.TestCallback callback) {
        ChargerTest.callback = callback;
    }

    public static void setMainActivity(MainActivity activity){
        mainActivity = activity;
    }
}