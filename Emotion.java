package com.example.emotiondetection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Emotion extends AppCompatActivity {
    ImageView iv;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE = 100;
    FaceDetector detector;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion);
        String permissions[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 42);
        }
        try {
            interpreter = new Interpreter(loadTensorModelFile(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initClickListener();
    }

    private MappedByteBuffer loadTensorModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("model/model.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
        fileChannel.close();
        return buffer;
    }

    private void initClickListener() {
        iv = findViewById(R.id.imgView);

        Button cameraButton = findViewById(R.id.camera_picture);
        cameraButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        Button storageButton = findViewById(R.id.storage_picture);
        storageButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                openGallery();
            }
        });

        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Emotion.this, MainActivity.class);
                startActivity(intent);
            }
        });

        detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_CLASSIFICATIONS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            iv.setScaleX(mScaleFactor);
            iv.setScaleY(mScaleFactor);
            return true;
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                iv.setImageBitmap(photo); // Set the selected image to ImageView
                detectAndPredictEmotion(photo); // Perform face detection and emotion prediction
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(photo); // Set the captured image to ImageView
            detectAndPredictEmotion(photo); // Perform face detection and emotion prediction
        }
    }

    private void detectAndPredictEmotion(Bitmap photo) {
        Frame frame = new Frame.Builder().setBitmap(photo).build();
        SparseArray<Face> faces = detector.detect(frame);

        // After detecting faces, pass the cropped face images to the predict method
        // Replace the following line with your face detection logic
        Bitmap faceImage = photo; // Replace this with the cropped face image

        String predictedEmotion = predictEmotion(faceImage);

        // Draw green box around the detected face
        if (faces.size() > 0) {
            Face face = faces.valueAt(0); // Assuming only one face is detected
            float x1 = face.getPosition().x;
            float y1 = face.getPosition().y;
            float x2 = x1 + face.getWidth();
            float y2 = y1 + face.getHeight();

            Bitmap tempBitmap = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(tempBitmap);
            canvas.drawBitmap(photo, 0, 0, null);

            Paint boxPaint = new Paint();
            boxPaint.setColor(Color.GREEN);
            boxPaint.setStyle(Paint.Style.STROKE);
            boxPaint.setStrokeWidth(2); // Make the box thinner
            canvas.drawRect(x1, y1, x2, y2, boxPaint);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.GREEN);
            textPaint.setTextSize(24);
            canvas.drawText(predictedEmotion, x1, y1 - 10, textPaint); // Display predicted emotion on the box

            iv.setImageBitmap(tempBitmap);
        }

        // Display predicted emotion as text on the screen
        // Replace the following line with your UI element to display the predicted emotion
        Toast.makeText(this, "Predicted Emotion: " + predictedEmotion, Toast.LENGTH_SHORT).show();
    }
    private String predictEmotion(Bitmap capturePhoto) {
        // Prediction logic here
        // (Replace this with your emotion prediction logic using TensorFlow Lite model)

        // Sample logic:
        float[][][][] inputValues = preprocessInput(capturePhoto);
        float[][] outputScores = runInference(inputValues);
        String predictedEmotion = postprocessOutput(outputScores);

        // For debugging purposes, you can print the input image dimensions
        int width = capturePhoto.getWidth();
        int height = capturePhoto.getHeight();
        System.out.println("Input Image Dimensions: " + width + " x " + height);

        return predictedEmotion;
    }

    private float[][][][] preprocessInput(Bitmap capturePhoto) {
        // Preprocess input image here
        // (Replace this with your preprocessing logic)
        // This method should prepare the image for input to the TensorFlow Lite model

        // For simplicity, let's assume the input image dimensions are 32x32 and 3 channels (RGB)
        int width = capturePhoto.getWidth();
        int height = capturePhoto.getHeight();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(capturePhoto, 48, 48, true);

        // Convert Bitmap to float[][][][] array
        float[][][][] inputValues = new float[1][48][48][3];
        for (int i = 0; i < 48; i++) {
            for (int j = 0; j < 48; j++) {
                int pixel = resizedBitmap.getPixel(j, i); // Note: j is width, i is height
                float red = Color.red(pixel) / 255.0f;
                float green = Color.green(pixel) / 255.0f;
                float blue = Color.blue(pixel) / 255.0f;
                inputValues[0][i][j][0] = blue;
                inputValues[0][i][j][1] = green;
                inputValues[0][i][j][2] = red;
            }
        }
        return inputValues;
    }

    private float[][] runInference(float[][][][] inputValues) {
        float[][] outputScores = new float[1][7]; // Assuming output size is 1x7 for 7 emotion classes

        interpreter.run(inputValues, outputScores); // Assuming 'interpreter' is your TensorFlow Lite interpreter

        return outputScores;
    }

    private String postprocessOutput(float[][] outputScores) {
        String[] emotions = {"Anger", "Disgust", "Fear", "Happiness", "Sadness", "Surprise", "Neutral"};
        int maxIndex = 0;
        for (int i = 1; i < 7; i++) {
            if (outputScores[0][i] > outputScores[0][maxIndex]) {
                maxIndex = i;
            }
        }
        String predictedEmotion = emotions[maxIndex];

        return predictedEmotion;
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
