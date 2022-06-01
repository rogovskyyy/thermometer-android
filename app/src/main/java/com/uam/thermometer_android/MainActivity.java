package com.uam.thermometer_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public ImageView iv;
    public Bitmap bitmap;
    public Canvas canvas;
    public Paint paint;
    public FFT fft;

    int samplingfrequency = 12000;
    int blockSize = 1024;

    double[] x;
    double[] y;
    double[] ampl;

    int frequency = 100;

    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

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

        fft = new FFT(1024);

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

    private void startThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (loop) {
                        drawView();
                        //Log.d("APP", "Dzialam w petli");
                        try { Thread.sleep(100); } catch (Throwable e) { }
                    }
                }
            }
        });
        t.start();
    }

    public void drawView() {

        readAudio();

        //for(int i = 0; i <= x.length - 1; i++) {
        //    Log.d("MIKROFON -> ", x[i] + "");
        //}

        canvas.drawColor(Color.BLACK);
        paint.setColor(Color.YELLOW);
        iv.setImageBitmap(bitmap);

        //double t;
        //double c;

        fft.fft(x, y);

        for (int i = 0; i < blockSize / 2; i++) {

            //t = (double) i / (double) samplingfrequency;
            //c = Math.sin(2 * Math.PI * frequency * t) * 50;

            //canvas.drawCircle((float) i + 2, (float) ((double) BASE - (double) 50.0  + (double) c), (float) 2.0, paint);
            canvas.drawCircle((float) i, (float) ((double) BASE - (double) 10.0  + (double) +  x[i]), (float) 2.0, paint);
        }

        iv.invalidate();
        //frequency += 20;
    }

    protected void readAudio() {

        short[] audioBuffer = new short[blockSize];
        int bufferSize = AudioRecord.getMinBufferSize(samplingfrequency, channelConfiguration, audioEncoding);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                samplingfrequency,
                channelConfiguration,
                audioEncoding,
                bufferSize
        );

        audioRecord.startRecording();

        int bufferReadResult = audioRecord.read(audioBuffer, 0, blockSize);

        for(int i = 0; i < blockSize && i < bufferReadResult; i++) {
            x[i] = (double) audioBuffer[i] / 32768.0;
        }
        audioRecord.stop();
    }
}