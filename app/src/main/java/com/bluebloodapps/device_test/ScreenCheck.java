package com.bluebloodapps.device_test;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebloodapps.device_test.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class ScreenCheck extends AppCompatActivity {
    private static final String TAG = ScreenCheck.class.getSimpleName();


    private MainActivity parentActivity;
    private Button continueBtn;
    private Button continueBtn2;
    private TextView title;
    private TextView subtitle;
    private ImageView image;
    private Button finishBtn;

    public static ScreenCallback callback;

    private boolean bIniciePrueba=false;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.bCheckPantalla =false;

        setContentView(R.layout.layout_screen_check_start);

        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subTitle);
        image = findViewById(R.id.image);
        continueBtn2 = findViewById(R.id.continueBtn2);
        finishBtn = findViewById(R.id.finishBtn);

        /*if (Cobertura.getActiva() == 1){
            findViewById(R.id.title).setVisibility(View.GONE);
            findViewById(R.id.subTitle).setVisibility(View.GONE);
            findViewById(R.id.image).setVisibility(View.GONE);
            findViewById(R.id.continueBtn).setVisibility(View.GONE);
            findViewById(R.id.checkOk).setVisibility(View.VISIBLE);
        } else {*/

            continueBtn = findViewById(R.id.continueBtn);
            //continueBtn.setOnClickListener(v -> parentActivity.changeCurrentFragment(new ScreenCheckStart()));

            continueBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    bIniciePrueba=true;
                    Intent in = new Intent(ScreenCheck.this, ScreenCheckTool.class);
                    startActivity(in);

                }
            });
    }

    @Override
    public void onResume() {

        if (bIniciePrueba)
        {
            if (MainActivity.bCheckPantalla)
            {
                continueBtn.setVisibility(View.GONE);
                continueBtn2.setVisibility(View.GONE);
                title.setText("¡Excelente!\n\nLa verificación de la pantalla tactil paso la prueba");
                subtitle.setVisibility(View.GONE);

                finishBtn.setVisibility(View.VISIBLE);
                finishBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        callback.onSuccess();
                    }
                });
            }
            else
            {
                continueBtn.setText("Reintentar");
                title.setText("¡Ups! La verificación de la pantalla tactil no paso la prueba.\nQuizas no lograste completar todos los puntos.\nSi quieres puedes reintentar... ");
                subtitle.setVisibility(View.GONE);
            }
        }
        super.onResume();
    }

    public static ScreenCallback getCallback() {
        return callback;
    }

    public static void setCallback(ScreenCallback callback) {
        ScreenCheck.callback = callback;
    }

    public interface ScreenCallback{
        void onSuccess();
    }
}
