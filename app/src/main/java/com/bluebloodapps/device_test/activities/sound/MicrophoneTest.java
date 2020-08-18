package com.bluebloodapps.device_test.activities.sound;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bluebloodapps.device_test.MainActivity;
import com.bluebloodapps.device_test.R;
import com.bluebloodapps.device_test.utils.Status;
import com.bluebloodapps.device_test.utils.TestType;

import java.util.ArrayList;
import java.util.Random;

public class MicrophoneTest extends AppCompatActivity {
    private Button continueBtn;
    TextView instructionsText;

    boolean timerRunning = false;

    String textoReconocido;
    String textoGenerado;

    public static MainActivity.TestCallback callback;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);
        generateRandomNumber();

        continueBtn = findViewById(R.id.continueBtn);

        continueBtn.setOnClickListener(v -> {
            continueBtn.setVisibility(View.GONE);
            instructionsText.setText("Decí ahora: " + textoGenerado);

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, 1234);
        });

        instructionsText = findViewById(R.id.instructionsText);
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

        textoGenerado = randomString;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> reconocimiento = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            textoReconocido = reconocimiento.get(0);

            if(textoReconocido == null){
                instructionsText.setText("Texto incorrecto");
                continueBtn.setText("Reintentar");
                continueBtn.setVisibility(View.VISIBLE);
                continueBtn.setOnClickListener(view -> {
                    continueBtn.setVisibility(View.GONE);
                    instructionsText.setText("Decí ahora: " + textoGenerado);

                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, 1234);
                });
            }

            if(textoReconocido.toUpperCase().contains(textoGenerado.toUpperCase())){
                instructionsText.setText("Texto correcto");
                continueBtn.setText("Continuar");
                continueBtn.setVisibility(View.VISIBLE);

                continueBtn.setOnClickListener(view -> {
                    Toast.makeText(MicrophoneTest.this, "¡Chequeo completado con éxito!", Toast.LENGTH_LONG).show();
                    mainActivity.updateTestStatus(TestType.MICROPHONE, new Status("Funcionando", R.color.green, true));
                    callback.onSuccess();
                    finish();
                });
            }else{
                instructionsText.setText("Texto incorrecto");
                continueBtn.setText("Reintentar");
                continueBtn.setVisibility(View.VISIBLE);
                continueBtn.setOnClickListener(view -> {
                    continueBtn.setVisibility(View.GONE);
                    instructionsText.setText("Decí ahora: " + textoGenerado);

                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, 1234);
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static MainActivity.TestCallback getCallback() {
        return callback;
    }

    public static void setCallback(MainActivity.TestCallback callback) {
        MicrophoneTest.callback = callback;
    }

    public static void setMainActivity(MainActivity activity){
        mainActivity = activity;
    }
}