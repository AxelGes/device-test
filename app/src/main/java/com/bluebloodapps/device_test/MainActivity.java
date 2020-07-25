package com.bluebloodapps.device_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static boolean bCheckPantalla = false;
    public static boolean bCheckSonido = false;
    public static boolean bCheckSonidoCall = false;


    private Button startTest;

    private TextView screenText, hardwareText, storageText, soundText, sensorsText, connectivityTest;

    private ImageView screenTactilCard, batteryCard, localStorageCard, externalStorageCard, mainSpeakerCard, callSpeakerCard, wifiCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screenTactilCard = this.findViewById(R.id.screenTactilCard);
        screenTactilCard.setOnClickListener(view -> getScreenStatus(() -> checkScreenStatus()));

        batteryCard = this.findViewById(R.id.batteryCard);
        batteryCard.setOnClickListener(view -> getBatteryStatus());

        localStorageCard = this.findViewById(R.id.localStorageCard);
        localStorageCard.setOnClickListener(view -> getStorageStatus());

        externalStorageCard = this.findViewById(R.id.externalStorageCard);
        externalStorageCard.setOnClickListener(view -> getSdStorageStatus());

        mainSpeakerCard = this.findViewById(R.id.mainSpeakerCard);
        mainSpeakerCard.setOnClickListener(view -> getSoundStatus(() -> checkSoundStatus()));

        callSpeakerCard = this.findViewById(R.id.callSpeakerCard);
        callSpeakerCard.setOnClickListener(view -> getSoundCallStatus(() -> checkSoundCallStatus()));

        wifiCard = this.findViewById(R.id.wifiCard);
        wifiCard.setOnClickListener(view -> getSignalStatus());



        //complete test
        startTest = this.findViewById(R.id.startTest);
        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getScreenStatus(new ScreenCheck.ScreenCallback() {
                    @Override
                    public void onSuccess() {
                        getSoundStatus(new SoundCheck.SoundCallback() {
                            @Override
                            public void onSuccess() {
                                getSoundCallStatus(new SoundCallCheck.SoundCallback() {
                                    @Override
                                    public void onSuccess() {
                                        checkScreenStatus();
                                        checkSoundStatus();
                                        checkSoundCallStatus();
                                        getBatteryStatus();
                                        getStorageStatus();
                                        getSignalStatus();
                                        getSdStorageStatus();
                                    }
                                });
                            }
                        });
                    }
                });

            }
        });


        Log.d(TAG, "onCreate: ");
    }

    public void getSignalStatus(){
        TextView wifiText = this.findViewById(R.id.wifiText);

        NetworkUtils networkUtils = new NetworkUtils(getApplicationContext());
        if(networkUtils.isNetworkAvailable()){
            wifiText.setText("¡Funcionando!");
            wifiText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else{
            wifiText.setText("No funcionando.");
            wifiText.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
    }

    public void checkScreenStatus(){
        TextView screenTactilText = this.findViewById(R.id.screenTactilText);
        if (bCheckPantalla) {
            screenTactilText.setText("¡Funcionando!");
            screenTactilText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }else{
            screenTactilText.setText("Error en el checkeo");
            screenTactilText.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
    }

    public void getScreenStatus(ScreenCheck.ScreenCallback callback){
        ScreenCheck.setCallback(callback);

        Intent in = new Intent(this, ScreenCheck.class);
        startActivity(in);
    }

    public void checkSoundStatus(){
        TextView mainSpeakerText = this.findViewById(R.id.mainSpeakerText);
        if (bCheckSonido) {
            mainSpeakerText.setText("¡Funcionando!");
            mainSpeakerText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }else{
            mainSpeakerText.setText("Error en el checkeo");
            mainSpeakerText.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
    }

    public void getSoundStatus(SoundCheck.SoundCallback callback){
        SoundCheck.setCallback(callback);

        Intent in = new Intent(this, SoundCheck.class);
        startActivity(in);
    }

    public void checkSoundCallStatus(){
        TextView callSpeakerText = this.findViewById(R.id.callSpeakerText);
        if (bCheckSonidoCall) {
            callSpeakerText.setText("¡Funcionando!");
            callSpeakerText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }else{
            callSpeakerText.setText("Error en el checkeo");
            callSpeakerText.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
    }

    public void getSoundCallStatus(SoundCallCheck.SoundCallback callback){
        SoundCallCheck.setCallback(callback);

        Intent in = new Intent(this, SoundCallCheck.class);
        startActivity(in);
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

        TextView storageText = this.findViewById(R.id.localStorageText);
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
            TextView externalStorageText = this.findViewById(R.id.externalStorageText);
            externalStorageText.setText("Memoria SD no detectada");
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
