package com.bluebloodapps.device_test.activities;

import android.annotation.SuppressLint;
import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bluebloodapps.device_test.MainActivity;
import com.bluebloodapps.device_test.R;
import com.bluebloodapps.device_test.utils.Status;
import com.bluebloodapps.device_test.utils.TestType;

public class GyroCheck extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate;

    private Button continueBtn;

    //0 = topLeft | 1 = topRight | 2 = bottomLeft | 3 = bottomRight | 4 = center

    View topLeft, topRight, bottomLeft, bottomRight, center;

    private boolean[] steps = new boolean[5];
    private CountDownTimer timer;
    private int currentCheck = -1;

    TextView timeText;

    boolean timerRunning = false;

    public float xmax,ymax;

    AnimatedView animatedView = null;
    ShapeDrawable mDrawable = new ShapeDrawable();
    public static int x;
    public static int y;

    public static MainActivity.TestCallback callback;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        topLeft = findViewById(R.id.topLeft);
        topRight = findViewById(R.id.topRight);
        bottomLeft = findViewById(R.id.bottomLeft);
        bottomRight = findViewById(R.id.bottomRight);
        center = findViewById(R.id.center);

        continueBtn = findViewById(R.id.continueBtn);

        timeText = findViewById(R.id.countdownText);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastUpdate = System.currentTimeMillis();

        animatedView = new AnimatedView(this);
        animatedView.setElevation(5F);
        ((FrameLayout)findViewById(R.id.relativeLayout)).addView(animatedView);

        Display display = getWindowManager().getDefaultDisplay();
        xmax = (float)display.getWidth() - 50;
        ymax = (float)display.getHeight() - 320;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x -= (int) event.values[0];
            y += (int) event.values[1];
        }
    }

    public class AnimatedView extends androidx.appcompat.widget.AppCompatImageView {
        static final int width = 50;
        static final int height = 50;

        public AnimatedView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            mDrawable = new ShapeDrawable(new OvalShape());
            mDrawable.getPaint().setColor(0xffffAC23);
            mDrawable.setBounds(x, y, x + width, y + height);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (x > xmax) {
                x = (int) xmax;
            } else if (x < 0) {
                x = 0;
            }
            if (y > ymax) {
                y = (int) ymax;
            } else if (y < 0) {
                y = 0;
            }
            mDrawable.setBounds(x, y, x + width, y + height);
            mDrawable.draw(canvas);

            View currentDot = getCollidingDot();
            if(currentDot != null && currentCheck != -1 && !steps[currentCheck]){
                timer = new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long l) {
                        if(currentCheck == -1){
                            cancel();
                            timerRunning = false;
                            timeText.setText("\nAvanzá al siguiente punto");
                        }else{
                            timeText.setText("" + ((l / 1000) + 1));
                        }
                    }

                    @Override
                    public void onFinish() {
                        if(currentCheck != -1){
                            steps[currentCheck] = true;
                            currentDot.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                            if(allDone()){
                                timeText.setText("¡Prueba finalizada!");
                                continueBtn.setVisibility(VISIBLE);
                                continueBtn.setOnClickListener(view -> {
                                    finish();
                                    Status status = new Status("Funcionando", R.color.green, true);
                                    mainActivity.updateTestStatus(TestType.SENSOR_GYRO, status);
                                    callback.onSuccess();
                                });
                            }else{
                                timeText.setText("¡Bien!\nAvanzá al siguiente punto");
                            }
                        }
                        timerRunning = false;
                    }
                };
                if(!timerRunning){
                    timer.start();
                    timerRunning = true;
                }
            }
            invalidate();
        }
    }

    private boolean allDone(){
        boolean val = true;
        for(boolean b : steps){
            if(!b){
                val = false;
            }
        }
        return val;
    }

    private View getCollidingDot(){
        if(isViewOverlapping(topLeft)) {
            currentCheck = 0;
            return topLeft;
        }
        if(isViewOverlapping(topRight)) {
            currentCheck = 1;
            return topRight;
        }
        if(isViewOverlapping(bottomLeft)) {
            currentCheck = 2;
            return bottomLeft;
        }
        if(isViewOverlapping(bottomRight)) {
            currentCheck = 3;
            return bottomRight;
        }
        if(isViewOverlapping(center)) {
            currentCheck = 4;
            return center;
        }
        currentCheck = -1;
        return null;
    }


    public boolean isViewOverlapping(View v1) {
        Rect R1 = new Rect();
        v1.getHitRect(R1);

        return x >= R1.left &&
        x + 50 <= R1.right &&
        y + 50 <= R1.bottom &&
        y >= R1.top;

    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static MainActivity.TestCallback getCallback() {
        return callback;
    }

    public static void setCallback(MainActivity.TestCallback callback) {
        GyroCheck.callback = callback;
    }

    public static void setMainActivity(MainActivity activity){
        mainActivity = activity;
    }
}