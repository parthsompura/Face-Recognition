package com.example.parthpathak.androidfacedetection;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnProgress;
    ProgressDialog progressDialog;
    private SparseArray<Face> sparseArray;
    private Canvas canvas;
    private Paint rectPaint;
    private Bitmap myBitmap;
    private Bitmap tempBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");

        imageView = (ImageView)findViewById(R.id.imageView);
        btnProgress = (Button)findViewById(R.id.btnProgress);

        myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.img_parth);
        imageView.setImageBitmap(myBitmap);

        rectPaint = new Paint();
        rectPaint.setStrokeWidth(5);
        rectPaint.setColor(Color.BLACK);
        rectPaint.setStyle(Paint.Style.STROKE);

        tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(),myBitmap.getHeight(), Bitmap.Config.RGB_565);
        canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(myBitmap,0,0,null);

        btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    private void displayFrames() {
        progressDialog.dismiss();
        if (sparseArray!=null) {
            for (int i = 0; i < sparseArray.size(); i++) {
                Face face = sparseArray.valueAt(i);
                float x1 = face.getPosition().x;
                float y1 = face.getPosition().y;
                float x2 = x1 + face.getWidth();
                float y2 = y1 + face.getHeight();
                RectF rectF = new RectF(x1, y1, x2, y2);
                canvas.drawRoundRect(rectF, 2, 2, rectPaint);
            }
            imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        }
    }

    class BackgroundTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            com.google.android.gms.vision.face.FaceDetector faceDetector = new com.google.android.gms.vision.face.FaceDetector.Builder(getApplicationContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                    .setMode(com.google.android.gms.vision.face.FaceDetector.FAST_MODE)
                    .build();
            if(!faceDetector.isOperational())
            {
                Toast.makeText(MainActivity.this, "Face Detector could not be set up on your device", Toast.LENGTH_SHORT).show();
            }else {
                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                sparseArray = faceDetector.detect(frame);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            displayFrames();
        }
    }
}
