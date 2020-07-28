package com.bluebloodapps.device_test.activities;

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

public class ButtonsHardwareCheck extends AppCompatActivity {

    public static ButtonsHardwareCallback callback;

    private static MainActivity mainActivity;

    Button cancelBtn;
    TextView subTitle;
    LinearLayout mainLayout;
    ConstraintLayout testLayout;
    ImageView image;

    Boolean volumeUpBtn = false;
    Boolean volumeDownBtn = false;
    Boolean powerBtn = false;


    EditText numbersEditText;

    String randomNumber;

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
            case KeyEvent.KEYCODE_POWER:
                if (volumeUpBtn && volumeDownBtn && !powerBtn){
                    powerBtn = true;
                    mainActivity.buttonsHardwareCheck = true;
                    callback.onSuccess();
                    finish();
                    return true;
                }
                Log.d("BOTON", "power: ");
                break;
        }

        return super.onKeyDown(keycode, e);
    }


    public ButtonsHardwareCheck.ButtonsHardwareCallback getCallback(){
        return callback;
    }

    public static void setCallback(ButtonsHardwareCheck.ButtonsHardwareCallback callback) {
        ButtonsHardwareCheck.callback = callback;
    }

    public static void setMainActivity(MainActivity activity){
        ButtonsHardwareCheck.mainActivity = activity;
    }

    public interface ButtonsHardwareCallback{
        void onSuccess();
    }
}
