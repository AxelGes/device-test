package com.bluebloodapps.device_test.activities.hardware;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bluebloodapps.device_test.MainActivity;
import com.bluebloodapps.device_test.R;
import com.bluebloodapps.device_test.utils.Status;
import com.bluebloodapps.device_test.utils.TestType;

public class ChargerTest extends AppCompatActivity{
    boolean timerRunning = false;
    private CountDownTimer timer;

    TextView timeText;
    Button continueBtn;

    boolean isCharging;


    public static MainActivity.TestCallback callback;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Status testStatus = new Status("Falta checkeo", R.color.grey_text, false);
        mainActivity.updateTestStatus(TestType.CHARGER, testStatus);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger);

        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

        timeText = findViewById(R.id.countdownText);
        continueBtn = findViewById(R.id.continueBtn);

        findViewById(R.id.startBtn).setOnClickListener(view -> {
            timeText.setText("");
            findViewById(R.id.startBtn).setVisibility(View.GONE);
            timer = new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long l) {
                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = registerReceiver(null, ifilter);

                    int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                    isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

                    if(!isCharging){
                        timeText.setText("Prueba fallida. ¡No desconectes el dispositivo!");
                        findViewById(R.id.startBtn).setVisibility(View.VISIBLE);
                        cancel();
                        timerRunning = false;
                    }else{
                        timeText.setText("Tiempo restante:\n" + ((l / 1000) + 1));
                    }
                }

                @Override
                public void onFinish() {
                    if(isCharging){
                        timeText.setText("¡Prueba finalizada!");

                        continueBtn.setOnClickListener(view -> {
                            finish();
                            Status status = new Status("Funcionando", R.color.green, true);
                            mainActivity.updateTestStatus(TestType.CHARGER, status);
                            callback.onSuccess();
                        });

                        continueBtn.setVisibility(View.VISIBLE);
                    }else{
                        findViewById(R.id.startBtn).setVisibility(View.VISIBLE);
                        timeText.setText("¡Prueba fallida!");
                    }
                    timerRunning = false;
                }
            };
            if(!timerRunning){
                timer.start();
                timerRunning = true;
            }
        });
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