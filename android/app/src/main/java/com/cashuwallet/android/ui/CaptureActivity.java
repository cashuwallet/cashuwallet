package com.cashuwallet.android.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.cashuwallet.android.R;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class CaptureActivity extends AppCompatActivity {

    private final int RequestCameraPermissionID = 1001;

    private SurfaceView cameraPreview;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        setContentView(R.layout.activity_capture);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int side = width < height ? width : height;
        int dim = (3*side)/4;
        findViewById(R.id.scan_width).getLayoutParams().width = dim;
        findViewById(R.id.scan_height).getLayoutParams().height = dim;

        BarcodeDetector barcodeDetector = new BarcodeDetector
                .Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 1) return;

                Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) vibrator.vibrate(100);

                Barcode barcode = qrcodes.valueAt(0);
                activityReturn(barcode.displayValue, null);
            }
        });

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .build();

        cameraPreview = findViewById(R.id.camera_preview);
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                startCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                stopCamera();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID:
                if (!checkPermissionGranted(Manifest.permission.CAMERA, permissions, grantResults)) {
                    activityReturn(null, getResources().getString(R.string.camera_permission_denied));
                    return;
                }
                startCamera();
                break;
        }
    }

    private boolean checkPermissionGranted(String requestedPermission, String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (permission.equals(requestedPermission)) {
                int grantResult = grantResults[i];
                return grantResult == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    private void activityReturn(String result, String message) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", result);
        resultIntent.putExtra("message", message);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void startCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA }, RequestCameraPermissionID);
            return;
        }
        try {
            cameraSource.start(cameraPreview.getHolder());
        } catch (IOException e) {
            activityReturn(null, getResources().getString(R.string.camera_initialization_error));
            return;
        }
        Camera camera = getCamera();
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            List<String> modes = params.getSupportedFocusModes();
            if (modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                camera.setParameters(params);
            }
        }
    }

    private void stopCamera() {
        cameraSource.stop();
    }

    private Camera getCamera() {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType() != Camera.class) continue;
            field.setAccessible(true);
            try {
                return (Camera) field.get(cameraSource);
            } catch (IllegalAccessException e) {
                return null;
            }
        }
        return null;
    }

}
