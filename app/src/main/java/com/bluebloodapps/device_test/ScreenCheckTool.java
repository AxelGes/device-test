package com.bluebloodapps.device_test;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bluebloodapps.device_test.R;

import androidx.appcompat.app.AppCompatActivity;

public class ScreenCheckTool extends AppCompatActivity {

    LinearLayout screen;
    int nCanvasW;
    int nCanvasH;
  //  int nCantCirculosH=30;
  //  int nCantCirculosW=15;

    int nCantCirculosH=10;
    int nCantCirculosW=5;

    int nPixelsCirculoH;
    int nPixelsCirculoW;
    LinearLayout[] rows;
    //LinearLayout[] columns;
    ImageView[][] circles;
    Boolean[][] checked;
    int nTotalPixels;
    int nChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.bCheckPantalla =false;

        setContentView(R.layout.activity_screen_check);

        screen=(LinearLayout) findViewById(R.id.screen);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        nCanvasW = displaymetrics.widthPixels;
        nCanvasH= displaymetrics.heightPixels;

        nPixelsCirculoW=nCanvasW/nCantCirculosW;
        nPixelsCirculoH=nCanvasH/nCantCirculosH;

        circles=new ImageView[nCantCirculosH][nCantCirculosW];
        checked=new Boolean[nCantCirculosH][nCantCirculosW];

        nTotalPixels=nCantCirculosH*nCantCirculosW;

        rows=new LinearLayout[nCantCirculosH];

        DrawCircles();

    }

    @SuppressLint("ClickableViewAccessibility")
    public void DrawCircles()
    {

        for (int g=0;g<nCantCirculosH;g++) {

            rows[g] = new LinearLayout(this);
            rows[g].setOrientation(LinearLayout.HORIZONTAL);

            screen.addView(rows[g]);

            for (int f = 0; f < nCantCirculosW; f++) {

                final int ff=f;
                final int gg=g;

                checked[g][f] =false;

                circles[g][f] = new ImageView(this);
                circles[g][f].setImageResource(R.drawable.graycircle);
                circles[g][f].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

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
                                circles[gg][ff].setImageResource(R.drawable.greencircle);
                                if (!(checked[gg][ff])) {
                                    checked[gg][ff] = true;
                                    nChecked = nChecked + 1;
                                }

                                break;
                            case MotionEvent.ACTION_MOVE:
                                circles[yH][xW].setImageResource(R.drawable.greencircle);
                                if (!(checked[yH][xW])) {
                                    checked[yH][xW] = true;
                                    nChecked = nChecked + 1;
                                }
                                Log.d("TAG", "moving: " + x + "," + y + "(" + xW + ", " + yH + ")");
                                break;
                        }

                        if (nChecked == nTotalPixels) {
                            Toast.makeText(ScreenCheckTool.this, "¡Chequeo completado con éxito!", Toast.LENGTH_LONG).show();
                            MainActivity.bCheckPantalla = true;

                            ScreenCheckTool.this.finish();
                        }
                        return true;
                    }
                });


                rows[g].addView(circles[g][f]);

                circles[g][f].getLayoutParams().height = nPixelsCirculoH;
                circles[g][f].getLayoutParams().width = nPixelsCirculoW;

            }
        }
    }
}
