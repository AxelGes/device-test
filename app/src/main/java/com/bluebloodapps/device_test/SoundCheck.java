package com.bluebloodapps.device_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;

public class SoundCheck extends AppCompatActivity {

    public static SoundCallback callback;

    Button continueBtn;
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
                                MainActivity.bCheckSonido = true;
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


        randomNumber = randomString;
        Log.d("Random", "RandomNumber: " + randomNumber);
    }

    public static SoundCallback getCallback() {
        return callback;
    }

    public static void setCallback(SoundCallback callback) {
        SoundCheck.callback = callback;
    }

    public interface SoundCallback{
        void onSuccess();
    }
}
