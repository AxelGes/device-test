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

    private Button continueBtn;
    TextView screenCheckSubTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        screenCheckSubTitle = this.findViewById(R.id.screenCheckSubTitle);
        continueBtn = this.findViewById(R.id.continueBtn);


        if (bCheckPantalla) {
            screenCheckSubTitle.setText("¡Chequeo Ok!");
            screenCheckSubTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        } else {
            continueBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent in = new Intent(getApplicationContext(), ScreenCheck.class);
                    startActivity(in);

                }
            });
        }

        //get phone info

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
            storageText.setTextColor(-166025);
        }

        //BATTERY
        int batteryLevel = (int) getBatteryLevel();

        TextView batteryText = this.findViewById(R.id.bateryText);
        batteryText.setText(batteryLevel + "%");
        if (batteryLevel < 15){
            batteryText.setTextColor(-166025);
        }

        Log.d(TAG, "onCreate: ");
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

    @Override
    public void onResume() {

        if (bCheckPantalla)
        {
            screenCheckSubTitle.setText("¡Chequeo Ok!") ;
            screenCheckSubTitle.setTextColor(ContextCompat.getColor(getApplication(), R.color.colorPrimary));
        }
        else
            screenCheckSubTitle.setText("Falta Chequeo") ;

        super.onResume();
    }
}
