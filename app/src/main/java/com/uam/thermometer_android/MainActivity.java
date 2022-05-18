package com.uam.thermometer_android;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    int samplingfrequency = 12000;
    int blockSize = 1024;

    double[] x;
    double[] y;
    double[] ampl;

    boolean loop = false;

    private final static float BASE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = new double[blockSize];
        y = new double[blockSize];

        ampl = new double[blockSize / 2];

        iv = (ImageView) this.findViewById(R.id.iv0);
        bitmap = Bitmap.createBitmap((int) blockSize / 2, (int) 410, Bitmap.Config.ARGB_8888);

        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);

        paint = new Paint();
        paint.setColor(Color.YELLOW);

        iv.setImageBitmap(bitmap);

        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(view -> {
            System.out.println("Zmieniam na true");
            loop = true;
        });

        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(view -> {
            System.out.println("Zmieniam na false");
            loop = false;
        });

        Log.d("APP", "Starting thread");
        this.startThread();
        Log.d("APP", "Started thread");

    }

    public void drawView() {
        for(int i = 0; i < blockSize / 2; i++) {
            //canvas.drawLine(i, BASE, i += 2, BASE - ((float) Math.sin(i) * 100), paint);
            Random rnd = new Random();
            int x = rnd.nextInt(100);
            canvas.drawCircle((float) i + 2, BASE - (float) x, (float) 2.0, paint);
        }
        iv.invalidate();
    }

    private void startThread() {
        Thread t = new Thread() {
            @Override
            public void run() {
                while(true) {
                    while (loop) {
                        drawView();
                        Log.d("APP", "Dzialam w petli mordo");
                    }
                }
            }
        };
        t.start();
    }

}