package com.bluebloodapps.device_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebloodapps.device_test.activities.ButtonsHardwareCheck;
import com.bluebloodapps.device_test.activities.ScreenCheck;
import com.bluebloodapps.device_test.activities.SoundCallCheck;
import com.bluebloodapps.device_test.activities.SoundCheck;
import com.bluebloodapps.device_test.utils.NetworkUtils;
import com.bluebloodapps.device_test.utils.Status;
import com.bluebloodapps.device_test.utils.TestType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static HashMap<TestType, Status> checksStatus;

    private Button startTest;

    private TextView screenTactilText, batteryText, localStorageText, externalStorageText, mainSpeakerText, callSpeakerText, sensor1Text, wifiText, buttonsText;

    private ImageView screenTactilCard, batteryCard, localStorageCard, externalStorageCard, mainSpeakerCard, callSpeakerCard, wifiCard, buttonsCard;

    public static boolean buttonsHardwareCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checksStatus = new HashMap<>();

        calcularPruebas();

        screenTactilText = findViewById(R.id.screenTactilText);
        batteryText = findViewById(R.id.batteryText);
        localStorageText = findViewById(R.id.localStorageText);
        externalStorageText = findViewById(R.id.externalStorageText);
        mainSpeakerText = findViewById(R.id.mainSpeakerText);
        callSpeakerText = findViewById(R.id.callSpeakerText);
        sensor1Text = findViewById(R.id.sensor1Text);
        wifiText = findViewById(R.id.wifiText);
        buttonsText = findViewById(R.id.buttonsText);

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

        buttonsCard = this.findViewById(R.id.buttonsCard);
        buttonsCard.setOnClickListener(view -> getButtonsStatus(() -> checkButtonsStatus()));

        //complete test
        startTest = this.findViewById(R.id.startTest);
        startTest.setOnClickListener(view -> getScreenStatus(() -> getSoundStatus(() -> getSoundCallStatus(() -> getButtonsStatus(() ->{
            checkScreenStatus();
            checkSoundStatus();
            checkButtonsStatus();
            checkSoundCallStatus();
            getBatteryStatus();
            getStorageStatus();
            getSignalStatus();
            getSdStorageStatus();
        })))));

        updateLayouts();
    }

    public void getButtonsStatus(ButtonsHardwareCheck.ButtonsHardwareCallback callback){
        ButtonsHardwareCheck.setCallback(callback);
        ButtonsHardwareCheck.setMainActivity(this);

        Intent in = new Intent(this, ButtonsHardwareCheck.class);
        startActivity(in);
    }

    public void checkButtonsStatus(){
        if (buttonsHardwareCheck){
            Status status = new Status("Funcionando", R.color.green, true);
            updateTestStatus(TestType.BUTTONS_HARDWARE, status);
        } else{
            Status status = new Status("Falta checkeo", R.color.grey, true);
            updateTestStatus(TestType.BUTTONS_HARDWARE, status);
        }
        updateLayouts();
    }

    public void getSignalStatus(){
        NetworkUtils networkUtils = new NetworkUtils(getApplicationContext());
        if(networkUtils.isNetworkAvailable()){
            Status status = new Status("Completado", R.color.green, true);
            updateTestStatus(TestType.WIFI, status);
        } else{
            Status status = new Status("Falta checkeo", R.color.grey, true);
            updateTestStatus(TestType.WIFI, status);
        }
    }

    public void checkScreenStatus(){
        screenTactilText.setText(checksStatus.get(TestType.SCREEN).message);
        screenTactilText.setTextColor(checksStatus.get(TestType.SCREEN).color);
    }

    public void getScreenStatus(ScreenCheck.ScreenCallback callback){
        ScreenCheck.setCallback(callback);
        ScreenCheck.setMainActivity(this);
        Intent in = new Intent(this, ScreenCheck.class);
        startActivity(in);
    }

    public void checkSoundStatus(){
        mainSpeakerText.setText(checksStatus.get(TestType.SOUND_MAIN).message);
        mainSpeakerText.setTextColor(checksStatus.get(TestType.SOUND_MAIN).color);
    }

    public void getSoundStatus(SoundCheck.SoundCallback callback){
        SoundCheck.setCallback(callback);
        SoundCheck.setMainActivity(this);

        Intent in = new Intent(this, SoundCheck.class);
        startActivity(in);
    }

    public void checkSoundCallStatus(){
        callSpeakerText.setText(checksStatus.get(TestType.SOUND_CALL).message);
        callSpeakerText.setTextColor(checksStatus.get(TestType.SOUND_CALL).color);
    }

    public void getSoundCallStatus(SoundCallCheck.SoundCallback callback){
        SoundCallCheck.setCallback(callback);
        SoundCallCheck.setMainActivity(this);

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


        if ((totalFloat / 4) > disponibleFloat){
            Status status = new Status(disponibleFloat + "GB de " + totalFloat + "GB", R.color.red, true);
            updateTestStatus(TestType.LOCAL_STORAGE, status);
        } else{
            Status status = new Status(disponibleFloat + "GB de " + totalFloat + "GB", R.color.green, true);
            updateTestStatus(TestType.LOCAL_STORAGE, status);
        }
        updateLayouts();
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
            Status status = new Status("Memoria SD no detectada", R.color.grey, true);
            updateTestStatus(TestType.EXTERNAL_STORAGE, status);
        }
        updateLayouts();
    }

    public void getBatteryStatus(){
        //BATTERY
        int batteryLevel = (int) getBatteryLevel();

        TextView batteryText = this.findViewById(R.id.batteryText);
        batteryText.setText(batteryLevel + "%");
        if (batteryLevel < 15){
            Status status = new Status(batteryLevel + "%", R.color.red, true);
            updateTestStatus(TestType.BATTERY, status);
        } else{
            Status status = new Status(batteryLevel + "%", R.color.green, true);
            updateTestStatus(TestType.BATTERY, status);
        }
        updateLayouts();
    }

    public float getBatteryLevel() {
        Intent batteryIntent = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

    public void calcularPruebas(){
        for(TestType testType : TestType.values()){
            if(testType != null){
                if(!PreferenceManager.getDefaultSharedPreferences(this).contains(testType.toString())){
                    Status status = new Status("Falta checkeo", R.color.grey_text,false);

                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString(testType.toString(),
                            status.message + "@@@" + status.color + "@@@" + status.done).apply();
                    checksStatus.put(testType, status);
                }else{
                    String statusObject = PreferenceManager.getDefaultSharedPreferences(this).getString(testType.toString(), "");
                    Status status = new Status(statusObject.split("@@@")[0],
                            Integer.parseInt(statusObject.split("@@@")[1]),
                            Boolean.parseBoolean(statusObject.split("@@@")[2]));
                    checksStatus.put(testType, status);
                }

            }
        }
    }

    public void updateTestStatus(TestType testType, Status status){
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(testType.toString(),
                status.message + "@@@" + status.color + "@@@" + status.done).apply();
        checksStatus.put(testType, status);

        updateLayouts();
    }

    public static boolean isDone(TestType type){
        return checksStatus.get(type).done;
    }

    public void updateLayouts(){
        screenTactilText.setText(checksStatus.get(TestType.SCREEN).message);
        screenTactilText.setTextColor(ContextCompat.getColor(this, checksStatus.get(TestType.SCREEN).color));

        batteryText.setText(checksStatus.get(TestType.SCREEN).message);
        batteryText.setTextColor(ContextCompat.getColor(this, checksStatus.get(TestType.SCREEN).color));

        localStorageText.setText(checksStatus.get(TestType.SCREEN).message);
        localStorageText.setTextColor(ContextCompat.getColor(this, checksStatus.get(TestType.SCREEN).color));

        externalStorageText.setText(checksStatus.get(TestType.SCREEN).message);
        externalStorageText.setTextColor(ContextCompat.getColor(this, checksStatus.get(TestType.SCREEN).color));

        mainSpeakerText.setText(checksStatus.get(TestType.SCREEN).message);
        mainSpeakerText.setTextColor(ContextCompat.getColor(this, checksStatus.get(TestType.SCREEN).color));

        callSpeakerText.setText(checksStatus.get(TestType.SCREEN).message);
        callSpeakerText.setTextColor(ContextCompat.getColor(this, checksStatus.get(TestType.SCREEN).color));

        wifiText.setText(checksStatus.get(TestType.SCREEN).message);
        wifiText.setTextColor(ContextCompat.getColor(this, checksStatus.get(TestType.SCREEN).color));
    }
}
