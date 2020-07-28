package com.bluebloodapps.device_test.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bluebloodapps.device_test.MainActivity;
import com.bluebloodapps.device_test.R;
import com.bluebloodapps.device_test.utils.Status;
import com.bluebloodapps.device_test.utils.TestType;

import java.util.Random;

public class SoundCheck extends AppCompatActivity {

    public static MainActivity.TestCallback callback;

    private static MainActivity mainActivity;

    Button continueBtn, cancelBtn, retryBtn;
    LinearLayout mainLayout;
    ConstraintLayout testLayout;

    EditText numbersEditText;

    String randomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_check);

        mainLayout = this.findViewById(R.id.mainLayout);
        testLayout = this.findViewById(R.id.testLayout);

        numbersEditText = this.findViewById(R.id.numbersEditText);

        continueBtn = this.findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.GONE);
                testLayout.setVisibility(View.VISIBLE);

                generateRandomNumber();

                numbersEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (numbersEditText.getText().toString().length() == 3){
                            if (numbersEditText.getText().toString().equals(randomNumber)){
                                Toast.makeText(SoundCheck.this, "¡Chequeo completado con éxito!", Toast.LENGTH_LONG).show();
                                Status status = new Status("Completado", R.color.green, true);
                                mainActivity.updateTestStatus(TestType.SOUND_MAIN, status);
                                callback.onSuccess();
                                finish();
                            } else{
                                numbersEditText.setText("");
                                Toast.makeText(SoundCheck.this, "¡Error! Intentelo de nuevo", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });

        cancelBtn = this.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(view -> {
            Status status = new Status("Falta checkeo", R.color.grey_text, false);
            mainActivity.updateTestStatus(TestType.SOUND_MAIN, status);
            callback.onSuccess();
            finish();
        });

        retryBtn = this.findViewById(R.id.retryBtn);
        retryBtn.setOnClickListener(view -> generateRandomNumber());

    }

    private void generateRandomNumber(){
        final int min = 0;
        final int max = 999;
        final int random = new Random().nextInt((max - min) + 1) + min;

        String randomString = "";
        if (random <= 9){
            randomString = "00" + random;
        }

        if (random < 100 && random > 9){
            randomString = "0" + random;
        }

        if (random >= 100){
            randomString = "" + random;
        }

        int[] sounds = {R.raw.uno, R.raw.dos, R.raw.tres};
        for (int i = 0; i < 3; i++){
            switch (randomString.charAt(i)) {
                case '0':
                    sounds[i] = R.raw.cero;
                    break;
                case '1':
                    sounds[i] = R.raw.uno;
                    break;
                case '2':
                    sounds[i] = R.raw.dos;
                    break;
                case '3':
                    sounds[i] = R.raw.tres;
                    break;
                case '4':
                    sounds[i] = R.raw.cuatro;
                    break;
                case '5':
                    sounds[i] = R.raw.cinco;
                    break;
                case '6':
                    sounds[i] = R.raw.seis;
                    break;
                case '7':
                    sounds[i] = R.raw.siete;
                    break;
                case '8':
                    sounds[i] = R.raw.ocho;
                    break;
                case '9':
                    sounds[i] = R.raw.nueve;
                    break;
            }
        }


        MediaPlayer mp1 = MediaPlayer.create( this, sounds[0]);
        mp1.start();

        mp1.setOnCompletionListener(mediaPlayer -> {
            mp1.release();
            MediaPlayer mp2 = MediaPlayer.create( getApplicationContext(), sounds[1]);
            mp2.start();
            mp2.setOnCompletionListener(mediaPlayer12 -> {
                mp2.release();
                MediaPlayer mp3 = MediaPlayer.create( getApplicationContext(), sounds[2]);
                mp3.start();
                mp3.setOnCompletionListener(mediaPlayer1 -> mp3.release());
            });
        });


        randomNumber = randomString;
        Log.d("Random", "RandomNumber: " + randomNumber);
    }

    public MainActivity.TestCallback getCallback(){
        return callback;
    }

    public static void setCallback(MainActivity.TestCallback callback) {
        SoundCheck.callback = callback;
    }

    public static void setMainActivity(MainActivity activity){
        SoundCheck.mainActivity = activity;
    }
}
