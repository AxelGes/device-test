package com.bluebloodapps.device_test.activities.screen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bluebloodapps.device_test.MainActivity;
import com.bluebloodapps.device_test.R;
import com.bluebloodapps.device_test.utils.Status;
import com.bluebloodapps.device_test.utils.TestType;

import androidx.appcompat.app.AppCompatActivity;

public class ScreenCheck extends AppCompatActivity {
    public static MainActivity mainActivity;

    LinearLayout screen;
    int nCanvasW;
    int nCanvasH;

    int nCantCirculosH = 20;
    int nCantCirculosW = 10;

    int nPixelsCirculoH;
    int nPixelsCirculoW;
    LinearLayout[] rows;
    //LinearLayout[] columns;
    ImageView[][] circles;
    Boolean[][] checked;
    int nTotalPixels;
    int nChecked;

    public static MainActivity.TestCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_check);

        Status status = new Status("Falta checkeo", R.color.grey_text, false);
        mainActivity.updateTestStatus(TestType.SCREEN, status);

        screen = findViewById(R.id.screen);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        nCanvasW = displaymetrics.widthPixels;
        nCanvasH = displaymetrics.heightPixels;

        nPixelsCirculoW = nCanvasW / nCantCirculosW;
        nPixelsCirculoH = nCanvasH / nCantCirculosH;

        circles = new ImageView[nCantCirculosH][nCantCirculosW];
        checked = new Boolean[nCantCirculosH][nCantCirculosW];

        nTotalPixels = nCantCirculosH * nCantCirculosW;

        rows = new LinearLayout[nCantCirculosH];

        findViewById(R.id.startBtn).setOnClickListener(view -> {
            DrawCircles();
            findViewById(R.id.startBtn).setVisibility(View.GONE);
        });

        findViewById(R.id.finishBtn).setOnClickListener(view -> {
            callback.onSuccess();
            finish();
            findViewById(R.id.finishBtn).setVisibility(View.GONE);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void DrawCircles() {
        for (int g = 0; g < nCantCirculosH; g++) {

            rows[g] = new LinearLayout(this);
            rows[g].setOrientation(LinearLayout.HORIZONTAL);

            screen.addView(rows[g]);

            for (int f = 0; f < nCantCirculosW; f++) {

                final int ff = f;
                final int gg = g;

                checked[g][f] = false;

                circles[g][f] = new ImageView(this);
                circles[g][f].setImageResource(R.drawable.graycircle);

                circles[g][f].setOnTouchListener((v, event) -> {
                    int x = (int) event.getRawX();
                    int y = (int) event.getRawY();

                    int xW = x / nPixelsCirculoW;
                    int yH = ((y - 50) / nPixelsCirculoH);

                    if (xW > nCantCirculosW - 1) {
                        xW = nCantCirculosW - 1;
                    }
                    if (yH > nCantCirculosH - 1) {
                        yH = nCantCirculosH - 1;
                    }

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_UP:
                            circles[gg][ff].setImageResource(R.drawable.circulo_verde);

                            // circles[gg][ff].setVisibility(View.GONE);

                            if (!(checked[gg][ff])) {
                                checked[gg][ff] = true;
                                nChecked = nChecked + 1;
                            }

                            break;
                        case MotionEvent.ACTION_MOVE:
                            circles[yH][xW].setImageResource(R.drawable.circulo_verde);

                            //circles[yH][xW].setVisibility(View.GONE);

                            if (!(checked[yH][xW])) {
                                checked[yH][xW] = true;
                                nChecked = nChecked + 1;
                            }
                            Log.d("TAG", "moving: " + x + "," + y + "(" + xW + ", " + yH + ")");
                            break;
                    }

                    if (nChecked == nTotalPixels) {
                        Status status = new Status("Completado", R.color.green, true);
                        mainActivity.updateTestStatus(TestType.SCREEN, status);

                        findViewById(R.id.finishBtn).setVisibility(View.VISIBLE);
                    }
                    return true;
                });


                rows[g].addView(circles[g][f]);

                circles[g][f].getLayoutParams().height = nPixelsCirculoH;
                circles[g][f].getLayoutParams().width = nPixelsCirculoW;
            }
        }
    }

    public static void setCallback(MainActivity.TestCallback callback) {
        ScreenCheck.callback = callback;
    }

    public static void setMainActivity(MainActivity activity){
        mainActivity = activity;
    }
}
