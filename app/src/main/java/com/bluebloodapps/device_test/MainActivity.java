package com.bluebloodapps.device_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static boolean bCheckPantalla = false;

    private Button startTest;
    TextView screenStatus, signalText, memoryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        screenStatus = this.findViewById(R.id.screenText);
        signalText = this.findViewById(R.id.signalText);
        memoryText = this.findViewById(R.id.memoryText);

        startTest = this.findViewById(R.id.startTest);


        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getScreenStatus(new ScreenCheck.ScreenCallback() {
                    @Override
                    public void onSuccess() {
                        getScreenStatus(() -> {

                        });
                        getBatteryStatus();
                        getStorageStatus();
                        getSignalStatus();
                        getSdStorageStatus();
                    }
                });

            }
        });


        Log.d(TAG, "onCreate: ");
    }

    public void getSignalStatus(){
        NetworkUtils networkUtils = new NetworkUtils(getApplicationContext());
        if(networkUtils.isNetworkAvailable()){
            signalText.setText("¡Funcionando!");
            signalText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else{
            signalText.setText("No funcionando.");
            signalText.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
    }

    public void getScreenStatus(ScreenCheck.ScreenCallback callback){
        if (bCheckPantalla) {
            screenStatus.setText("¡Funcionando!");
            screenStatus.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }else{
            ScreenCheck.setCallback(callback);

            Intent in = new Intent(this, ScreenCheck.class);
            startActivity(in);
        }
    }

    public void getStorageStatus(){
        //STORAGE
        @SuppressLint("UsableSpace") float bytesTotal = Environment.getDataDirectory().getTotalSpace();
        float gbTotal = bytesTotal / 1000000000;
        BigDecimal total = new BigDecimal(Float.toString(gbTotal));
        float totalFloat = total.setScale(2, RoundingMode.DOWN).floatValue();

        @SuppressLint("UsableSpace") float bytesAvailable = Environment.getDataDirectory().getUsableSpace();
        float gbAvailable = bytesAvailable / 1000000000;
        BigDecimal disponible = new BigDecimal(Float.toString(gbAvailable));
        float disponibleFloat = disponible.setScale(2, RoundingMode.DOWN).floatValue();

        TextView storageText = this.findViewById(R.id.storageText);
        storageText.setText(disponibleFloat + "GB de " + totalFloat + "GB");
        if ((totalFloat / 4) > disponibleFloat){
            storageText.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
    }

    public void getSdStorageStatus(){
        // SD STORAGE
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();

        if(isSDSupportedDevice && isSDPresent)
        {
            // yes SD-card is present
        }
        else
        {
            memoryText.setText("Memoria SD no detectada");
        }
    }

    public void getBatteryStatus(){
        //BATTERY
        int batteryLevel = (int) getBatteryLevel();

        TextView batteryText = this.findViewById(R.id.batteryText);
        batteryText.setText(batteryLevel + "%");
        if (batteryLevel < 15){
            batteryText.setTextColor(ContextCompat.getColor(this, R.color.red));
        } else{
            batteryText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        }
    }

    public float getBatteryLevel() {
        Intent batteryIntent = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }
}
