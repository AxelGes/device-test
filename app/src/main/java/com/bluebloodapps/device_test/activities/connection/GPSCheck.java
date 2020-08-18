package com.bluebloodapps.device_test.activities.connection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bluebloodapps.device_test.MainActivity;
import com.bluebloodapps.device_test.R;
import com.bluebloodapps.device_test.utils.Status;
import com.bluebloodapps.device_test.utils.TestType;

public class GPSCheck extends AppCompatActivity implements LocationListener {
    TextView instructionsText;
    Button continueBtn;

    protected LocationManager locationManager;

    boolean isRunning;

    public static MainActivity.TestCallback callback;
    public static MainActivity mainActivity;

    private static final int REQUEST_LOCATION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        Status testStatus = new Status("Falta checkeo", R.color.grey_text, false);
        mainActivity.updateTestStatus(TestType.GPS, testStatus);

        instructionsText = findViewById(R.id.instructionsText);
        continueBtn = findViewById(R.id.continueBtn);

        instructionsText.setText("Habilitá los servicios de ubicación para nuestra app.");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        } else {
            instructionsText.setText(null);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        continueBtn.setText("Comenzar prueba");
        continueBtn.setOnClickListener(view -> {
            continueBtn.setVisibility(View.GONE);
            isRunning = true;
            instructionsText.setText("Perfecto, ahora salí al aire libre para que podamos probar el estado del GPS.");
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    instructionsText.setText("Habilitá los servicios de ubicación para nuestra app.");
                    return;
                }
                instructionsText.setText(null);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
    }

    public static MainActivity.TestCallback getCallback() {
        return callback;
    }

    public static void setCallback(MainActivity.TestCallback callback) {
        GPSCheck.callback = callback;
    }

    public static void setMainActivity(MainActivity activity){
        mainActivity = activity;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(isRunning){
            isRunning = false;
            instructionsText.setText("¡GPS funcionando correctamente!");

            continueBtn.setVisibility(View.VISIBLE);
            continueBtn.setText("Siguiente");

            continueBtn.setOnClickListener(view -> {
                Status status = new Status("Funcionando", R.color.green, true);
                mainActivity.updateTestStatus(TestType.GPS, status);

                callback.onSuccess();
                finish();
            });
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}