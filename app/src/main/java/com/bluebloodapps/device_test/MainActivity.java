package com.bluebloodapps.device_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebloodapps.device_test.activities.connection.GPSCheck;
import com.bluebloodapps.device_test.activities.hardware.ButtonsHardwareCheck;
import com.bluebloodapps.device_test.activities.hardware.ChargerTest;
import com.bluebloodapps.device_test.activities.hardware.GyroCheck;
import com.bluebloodapps.device_test.activities.sound.MicrophoneTest;
import com.bluebloodapps.device_test.activities.hardware.ProximityCheck;
import com.bluebloodapps.device_test.activities.screen.ScreenCheck;
import com.bluebloodapps.device_test.activities.sound.SoundCallCheck;
import com.bluebloodapps.device_test.activities.sound.SoundCheck;
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

    private TextView screenTactilText, batteryText, localStorageText, externalStorageText, mainSpeakerText, callSpeakerText, gyroText, wifiText, buttonsText, chargerText, proximityText, microphoneText, gpsText, mobileText, btText;

    private ImageView batteryCard;
    private ImageView localStorageCard;
    private ImageView externalStorageCard;
    private ImageView mainSpeakerCard;
    private ImageView callSpeakerCard;
    private ImageView wifiCard;
    private ImageView mobileCard;
    private ImageView gpsCard;
    private ImageView buttonsCard;
    private ImageView gyroCard;
    private ImageView proximityCard;
    private ImageView chargerCard;
    private ImageView microphoneCard;
    private ImageView screenTactilCard;
    private ImageView btCard;

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
        gyroText = findViewById(R.id.gyroText);
        wifiText = findViewById(R.id.wifiText);
        btText = findViewById(R.id.btText);
        mobileText = findViewById(R.id.mobileText);
        buttonsText = findViewById(R.id.buttonsText);
        chargerText = findViewById(R.id.chargerText);
        proximityText = findViewById(R.id.proximityText);
        microphoneText = findViewById(R.id.microphoneText);
        gpsText = findViewById(R.id.gpsText);

        screenTactilCard = findViewById(R.id.screenTactilCard);
        screenTactilCard.setOnClickListener(view -> getScreenStatus(() -> checkScreenStatus()));

        chargerCard = this.findViewById(R.id.chargerCard);
        chargerCard.setOnClickListener(view -> getChargerStatus(() -> checkChargerStatus()));

        batteryCard = this.findViewById(R.id.batteryCard);
        batteryCard.setOnClickListener(view -> getBatteryStatus());
        getBatteryStatus();

        localStorageCard = this.findViewById(R.id.localStorageCard);
        localStorageCard.setOnClickListener(view -> getStorageStatus());
        getStorageStatus();

        externalStorageCard = this.findViewById(R.id.externalStorageCard);
        externalStorageCard.setOnClickListener(view -> getSdStorageStatus());
        getSdStorageStatus();

        mainSpeakerCard = this.findViewById(R.id.mainSpeakerCard);
        mainSpeakerCard.setOnClickListener(view -> getSoundStatus(() -> checkSoundStatus()));

        callSpeakerCard = this.findViewById(R.id.callSpeakerCard);
        callSpeakerCard.setOnClickListener(view -> getSoundCallStatus(() -> checkSoundCallStatus()));

        wifiCard = this.findViewById(R.id.wifiCard);
        wifiCard.setOnClickListener(view -> getWifiStatus());
        getWifiStatus();

        mobileCard = this.findViewById(R.id.mobileCard);
        mobileCard.setOnClickListener(view -> getMobileStatus());
        getMobileStatus();

        btCard = this.findViewById(R.id.btCard);
        btCard.setOnClickListener(view -> getBTStatus());
        getBTStatus();

        buttonsCard = this.findViewById(R.id.buttonsCard);
        buttonsCard.setOnClickListener(view -> getButtonsStatus(() -> checkButtonsStatus()));

        gyroCard = findViewById(R.id.gyroCard);
        gyroCard.setOnClickListener(view -> getGyroscopeStatus(() -> checkGyroscope()));

        proximityCard = findViewById(R.id.proximityCard);
        proximityCard.setOnClickListener(view -> getProximityStatus(() -> checkProximity()));

        proximityCard = findViewById(R.id.proximityCard);
        proximityCard.setOnClickListener(view -> getProximityStatus(() -> checkProximity()));

        microphoneCard = findViewById(R.id.microphoneCard);
        microphoneCard.setOnClickListener(view -> getMicrophoneStatus(() -> checkMicrophone()));

        gpsCard = findViewById(R.id.gpsCard);
        gpsCard.setOnClickListener(view -> getGPSStatus(() -> checkGPS()));

        //complete test
        startTest = this.findViewById(R.id.startTest);
        startTest.setOnClickListener(view -> getScreenStatus(() -> getSoundStatus(() -> getSoundCallStatus(() -> getButtonsStatus(() ->{
            checkScreenStatus();
            checkSoundStatus();
            checkButtonsStatus();
            checkSoundCallStatus();
            getBatteryStatus();
            getStorageStatus();
            getWifiStatus();
            getSdStorageStatus();
            checkProximity();
            checkMicrophone();
            checkGyroscope();
        })))));

        updateLayouts();
    }

    public void getGPSStatus(TestCallback callback){
        GPSCheck.setCallback(callback);
        GPSCheck.setMainActivity(this);

        Intent in = new Intent(this, GPSCheck.class);
        startActivity(in);
    }

    public void checkGPS(){
        gpsText.setText(checksStatus.get(TestType.GPS).message);
        gpsText.setTextColor(checksStatus.get(TestType.GPS).color);
    }

    public void getMicrophoneStatus(TestCallback callback){
        MicrophoneTest.setCallback(callback);
        MicrophoneTest.setMainActivity(this);

        Intent in = new Intent(this, MicrophoneTest.class);
        startActivity(in);
    }

    public void checkMicrophone(){
        microphoneText.setText(checksStatus.get(TestType.MICROPHONE).message);
        microphoneText.setTextColor(checksStatus.get(TestType.MICROPHONE).color);
    }

    public void getProximityStatus(TestCallback callback){
        ProximityCheck.setCallback(callback);
        ProximityCheck.setMainActivity(this);

        Intent in = new Intent(this, ProximityCheck.class);
        startActivity(in);
    }

    public void checkProximity(){
        proximityText.setText(checksStatus.get(TestType.PROXIMITY).message);
        proximityText.setTextColor(checksStatus.get(TestType.PROXIMITY).color);
    }

    public void getGyroscopeStatus(TestCallback callback){
        GyroCheck.setCallback(callback);
        GyroCheck.setMainActivity(this);

        Intent in = new Intent(this, GyroCheck.class);
        startActivity(in);
    }

    public void checkGyroscope(){
        gyroText.setText(checksStatus.get(TestType.SENSOR_GYRO).message);
        gyroText.setTextColor(checksStatus.get(TestType.SENSOR_GYRO).color);
    }

    public void getButtonsStatus(TestCallback callback){
        ButtonsHardwareCheck.setCallback(callback);
        ButtonsHardwareCheck.setMainActivity(this);

        Intent in = new Intent(this, ButtonsHardwareCheck.class);
        startActivity(in);
    }

    public void checkButtonsStatus(){
        buttonsText.setText(checksStatus.get(TestType.BUTTONS_HARDWARE).message);
        buttonsText.setTextColor(checksStatus.get(TestType.BUTTONS_HARDWARE).color);
    }

    public void getBTStatus(){
        NetworkUtils networkUtils = new NetworkUtils(getApplicationContext());
        if(networkUtils.isBluetoothOn()){
            Status status = new Status("Completado", R.color.green, true);
            updateTestStatus(TestType.BLUETOOTH, status);
        } else{
            Status status = new Status("Desconectado", R.color.grey_text, true);
            updateTestStatus(TestType.BLUETOOTH, status);
        }
    }

    public void getWifiStatus(){
        NetworkUtils networkUtils = new NetworkUtils(getApplicationContext());
        if(networkUtils.isWifiOn()){
            Status status = new Status("Completado", R.color.green, true);
            updateTestStatus(TestType.WIFI, status);
        } else{
            Status status = new Status("Desconectado", R.color.grey_text, true);
            updateTestStatus(TestType.WIFI, status);
        }
    }

    public void getMobileStatus(){
        NetworkUtils networkUtils = new NetworkUtils(getApplicationContext());
        if(networkUtils.isMobileOn()){
            Status status = new Status("Completado", R.color.green, true);
            updateTestStatus(TestType.MOBILE, status);
        }else{
            Status status = new Status("Desconectado", R.color.grey_text, true);
            updateTestStatus(TestType.MOBILE, status);
        }
    }

    public void checkScreenStatus(){
        screenTactilText.setText(checksStatus.get(TestType.SCREEN).message);
        screenTactilText.setTextColor(checksStatus.get(TestType.SCREEN).color);
    }

    public void getScreenStatus(TestCallback callback){
        ScreenCheck.setCallback(callback);
        ScreenCheck.setMainActivity(this);
        Intent in = new Intent(this, ScreenCheck.class);
        startActivity(in);
    }

    public void getChargerStatus(TestCallback callback){
        ChargerTest.setCallback(callback);
        ChargerTest.setMainActivity(this);

        Intent in = new Intent(this, ChargerTest.class);
        startActivity(in);
    }

    public void checkChargerStatus(){
        chargerText.setText(checksStatus.get(TestType.CHARGER).message);
        chargerText.setTextColor(checksStatus.get(TestType.CHARGER).color);
    }

    public void checkSoundStatus(){
        mainSpeakerText.setText(checksStatus.get(TestType.SOUND_MAIN).message);
        mainSpeakerText.setTextColor(checksStatus.get(TestType.SOUND_MAIN).color);
    }

    public void getSoundStatus(TestCallback callback){
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

    public static long getAvailableSpaceInGB(){
        final long SIZE_KB = 1024L;
        final long SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        return availableSpace/SIZE_GB;
    }

    public void getSdStorageStatus(){
        boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        if(isSDPresent) {
            Status status = new Status(getAvailableSpaceInGB() + "GB disponibles", R.color.green, true);
            updateTestStatus(TestType.EXTERNAL_STORAGE, status);
            // yes SD-card is present
        } else {
            Status status = new Status("Memoria SD no detectada", R.color.grey_text, true);
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
            if(!PreferenceManager.getDefaultSharedPreferences(this).contains(testType.toString())){
                Status status = new Status("Falta checkeo", ContextCompat.getColor(this, R.color.grey_text),false);

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

    public void updateTestStatus(TestType testType, Status status){
        Log.d("COLOR", ""+ContextCompat.getColor(this, status.color));

        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(testType.toString(),status.message + "@@@" + ContextCompat.getColor(this, status.color) + "@@@" + status.done).apply();

        status.color =  ContextCompat.getColor(this, status.color);

        checksStatus.put(testType, status);

        updateLayouts();
    }

    public static boolean isDone(TestType type){
        return checksStatus.get(type).done;
    }

    public void updateLayouts(){
        screenTactilText.setText(checksStatus.get(TestType.SCREEN).message);
        screenTactilText.setTextColor(checksStatus.get(TestType.SCREEN).color);

        batteryText.setText(checksStatus.get(TestType.BATTERY).message);
        batteryText.setTextColor(checksStatus.get(TestType.BATTERY).color);

        localStorageText.setText(checksStatus.get(TestType.LOCAL_STORAGE).message);
        localStorageText.setTextColor(checksStatus.get(TestType.LOCAL_STORAGE).color);

        externalStorageText.setText(checksStatus.get(TestType.EXTERNAL_STORAGE).message);
        externalStorageText.setTextColor(checksStatus.get(TestType.EXTERNAL_STORAGE).color);

        mainSpeakerText.setText(checksStatus.get(TestType.SOUND_MAIN).message);
        mainSpeakerText.setTextColor(checksStatus.get(TestType.SOUND_MAIN).color);

        callSpeakerText.setText(checksStatus.get(TestType.SOUND_CALL).message);
        callSpeakerText.setTextColor(checksStatus.get(TestType.SOUND_CALL).color);

        wifiText.setText(checksStatus.get(TestType.WIFI).message);
        wifiText.setTextColor(checksStatus.get(TestType.WIFI).color);

        btText.setText(checksStatus.get(TestType.BLUETOOTH).message);
        btText.setTextColor(checksStatus.get(TestType.BLUETOOTH).color);

        mobileText.setText(checksStatus.get(TestType.MOBILE).message);
        mobileText.setTextColor(checksStatus.get(TestType.MOBILE).color);

        buttonsText.setText(checksStatus.get(TestType.BUTTONS_HARDWARE).message);
        buttonsText.setTextColor(checksStatus.get(TestType.BUTTONS_HARDWARE).color);

        buttonsText.setText(checksStatus.get(TestType.PROXIMITY).message);
        buttonsText.setTextColor(checksStatus.get(TestType.PROXIMITY).color);
    }

    public interface TestCallback{
        void onSuccess();
    }
}
