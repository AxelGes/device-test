package com.bluebloodapps.device_test;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SoundCallCheck extends AppCompatActivity {

    public static SoundCallback callback;

    Button continueBtn, cancelBtn, retryBtn;
    LinearLayout mainLayout;
    ConstraintLayout testLayout;

    TextView subTitle;

    EditText numbersEditText;

    String randomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_check);

        mainLayout = this.findViewById(R.id.mainLayout);
        testLayout = this.findViewById(R.id.testLayout);

        numbersEditText = this.findViewById(R.id.numbersEditText);

        subTitle = this.findViewById(R.id.subTitle);
        subTitle.setText("¡Escuchá los 3 números que se oyen por el auricular y escribilos en la siguiente pantalla!");

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
                                Toast.makeText(SoundCallCheck.this, "¡Chequeo completado con éxito!", Toast.LENGTH_LONG).show();
                                MainActivity.bCheckSonidoCall = true;
                                callback.onSuccess();
                                finish();
                            } else{
                                numbersEditText.setText("");
                                Toast.makeText(SoundCallCheck.this, "¡Error! Intentelo de nuevo", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });

        cancelBtn = this.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.bCheckSonidoCall = false;
                callback.onSuccess();
                finish();
            }
        });

        retryBtn = this.findViewById(R.id.retryBtn);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomNumber();
            }
        });

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


        MediaPlayer mp1 = new MediaPlayer();
        try {
            mp1.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            mp1.setDataSource(this, Uri.parse("android.resource://com.bluebloodapps.device_test/" + sounds[0]));
            mp1.prepare();
            mp1.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mp1.setOnCompletionListener(mediaPlayer -> {
            mp1.release();

            MediaPlayer mp2 = new MediaPlayer();
            try {
                mp2.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                mp2.setDataSource(this, Uri.parse("android.resource://com.bluebloodapps.device_test/" + sounds[1]));
                mp2.prepare();
                mp2.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mp2.setOnCompletionListener(mediaPlayer1 -> {
                mp2.release();
                MediaPlayer mp3 = new MediaPlayer();
                try {
                    mp3.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                    mp3.setDataSource(this, Uri.parse("android.resource://com.bluebloodapps.device_test/" + sounds[2]));
                    mp3.prepare();
                    mp3.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp3.setOnCompletionListener(mediaPlayer2 -> {
                    mp3.release();
                });
            });
        });

        randomNumber = randomString;
        Log.d("Random", "RandomNumber: " + randomNumber);
    }

    public static SoundCallback getCallback(){
        return callback;
    }

    public static void setCallback(SoundCallback callback) {
        SoundCallCheck.callback = callback;
    }

    public interface SoundCallback{
        void onSuccess();
    }
}
