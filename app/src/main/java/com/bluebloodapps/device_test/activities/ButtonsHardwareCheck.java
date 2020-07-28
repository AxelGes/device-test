package com.bluebloodapps.device_test.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluebloodapps.device_test.MainActivity;
import com.bluebloodapps.device_test.R;
import com.bluebloodapps.device_test.utils.Status;
import com.bluebloodapps.device_test.utils.TestType;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ButtonsHardwareCheck extends Activity {

    public static MainActivity.TestCallback callback;

    private static MainActivity mainActivity;

    Button cancelBtn;
    TextView subTitle;
    LinearLayout mainLayout;
    ConstraintLayout testLayout;
    ImageView image;

    boolean volumeUpBtn = false;
    boolean volumeDownBtn = false;
    boolean powerBtn = false;
    boolean locked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttons_hardware_check);


        mainLayout = this.findViewById(R.id.mainLayout);
        testLayout = this.findViewById(R.id.testLayout);

        subTitle = this.findViewById(R.id.subTitle);

        image = this.findViewById(R.id.image);

        cancelBtn = this.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(view -> {
            Status status = new Status("Falta checkeo", R.color.grey_text, false);
            mainActivity.updateTestStatus(TestType.BUTTONS_HARDWARE, status);
            callback.onSuccess();
            finish();
        });

    }

    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (!volumeUpBtn && !volumeDownBtn && !powerBtn){
                    volumeUpBtn = true;
                    image.setImageResource(R.drawable.minus);
                    subTitle.setText("Ahora clickeá el botón bajar volumen");
                    return true;
                }

                Log.d("BOTON", "VOLUMEN UP: ");
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (volumeUpBtn && !volumeDownBtn && !powerBtn){
                    image.setImageResource(R.drawable.power);
                    volumeDownBtn = true;
                    subTitle.setText("Ahora clickeá el botón de bloqueo.");
                    return true;
                }
                Log.d("BOTON", "volumen down: ");
                break;
        }

        return super.onKeyDown(keycode, e);
    }

    @Override
    protected void onPause() {
        if (volumeUpBtn && volumeDownBtn && !powerBtn){
            locked = true;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (volumeUpBtn && volumeDownBtn && !powerBtn && locked){
            powerBtn = true;
            locked = false;
            showSuccessLayout();
        }
        super.onResume();
    }

    private void showSuccessLayout(){
        image.setImageResource(R.drawable.tick_02);
        cancelBtn.setText("Siguiente");
        subTitle.setText("¡Prueba realizada correctamente! Todos los botones funcionan.");
        cancelBtn.setOnClickListener(view -> {
            Status status = new Status("Funcionando", R.color.green, true);
            mainActivity.updateTestStatus(TestType.BUTTONS_HARDWARE, status);
            callback.onSuccess();
            finish();
        });
    }

    public MainActivity.TestCallback getCallback(){
        return callback;
    }

    public static void setCallback(MainActivity.TestCallback callback) {
        ButtonsHardwareCheck.callback = callback;
    }

    public static void setMainActivity(MainActivity activity){
        ButtonsHardwareCheck.mainActivity = activity;
    }
}
