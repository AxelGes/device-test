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

    private TextView screenText, hardwareText, storageText, soundText, sensorsText, connectivityTest;

    private ImageView screenTactilCard, batteryCard, localStorageCard, externalStorageCard, mainSpeakerCard, callSpeakerCard, wifiCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checksStatus = new HashMap<>();

        calcularPruebas();

        screenText = findViewById(R.id.screenText);
        hardwareText = findViewById(R.id.hardwareText);
        storageText = findViewById(R.id.storageText);
        soundText = findViewById(R.id.soundText);
        sensorsText = findViewById(R.id.sensorsText);
        connectivityTest = findViewById(R.id.signalText);

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
        startTest.setOnClickListener(view -> getScreenStatus(() -> getSoundStatus(() -> getSoundCallStatus(() -> {
            checkScreenStatus();
            checkSoundStatus();
            checkSoundCallStatus();
            getBatteryStatus();
            getStorageStatus();
            getSignalStatus();
            getSdStorageStatus();
        }))));

        updateLayouts();
    }

    public void getSignalStatus(){
        NetworkUtils networkUtils = new NetworkUtils(getApplicationContext());
        if(networkUtils.isNetworkAvailable()){
            Status status = new Status("Funcionando", R.color.green, true);
            updateTestStatus(TestType.NETWORK, status);
        } else{
            Status status = new Status("No funcionando", R.color.red, true);
            updateTestStatus(TestType.NETWORK, status);
        }
    }

    public void checkScreenStatus(){
        TextView screenTactilText = this.findViewById(R.id.screenTactilText);
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
        TextView mainSpeakerText = this.findViewById(R.id.mainSpeakerText);
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
        TextView callSpeakerText = this.findViewById(R.id.callSpeakerText);
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
        screenText.setText(checksStatus.get(TestType.SCREEN).message);
        screenText.setTextColor(ContextCompat.getColor(this, checksStatus.get(TestType.SCREEN).color));

        soundText.setText(checksStatus.get(TestType.SOUND_MAIN).message);
        soundText.setTextColor(ContextCompat.getColor(this, checksStatus.get(TestType.SOUND_MAIN).color));

        connectivityTest.setText(checksStatus.get(TestType.NETWORK).message);
        connectivityTest.setTextColor(ContextCompat.getColor(this, checksStatus.get(TestType.NETWORK).color));
    }
}
