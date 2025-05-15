package com.example.coinly;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRScanner extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int BUTTON_LAYOUT_DP = 20;

    private PreviewView pvScanner;
    private View scannerOverlay;
    private LinearLayout buttonLayout;

    private BarcodeScanner scanner;

    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        pvScanner = findViewById(R.id.preview_view_scanner);
        scannerOverlay = findViewById(R.id.scanner_overlay_view);
        buttonLayout = findViewById(R.id.button_layout);

        scannerOverlay.post(this::initButtonLayoutPosition);

        initScanner();
        initButtons();
    }

    private void initScanner() {
        scanner = BarcodeScanning.getClient();
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }
    }

    private void initButtonLayoutPosition() {
        if (!(scannerOverlay instanceof ScannerOverlayView)) {
            return;
        }

        RectF rect = ((ScannerOverlayView) scannerOverlay).getScannerRect();

        float dp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, BUTTON_LAYOUT_DP,
                getResources().getDisplayMetrics()
        );

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) buttonLayout.getLayoutParams();
        params.topMargin = (int)(rect.bottom + dp);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

        buttonLayout.setLayoutParams(params);
    }

    private void initButtons() {
        ImageButton imgBtnUpload = findViewById(R.id.image_button_upload);
        Button btnUpload = findViewById(R.id.button_upload);
        ImageButton imgBtnGenerate = findViewById(R.id.image_button_generate_qr);
        Button btnGenerate = findViewById(R.id.button_generate_qr);
        ImageButton imgBtnUseNum = findViewById(R.id.image_button_use_number);
        Button btnUseNum = findViewById(R.id.button_use_number);
        Button btnBack = findViewById(R.id.button_back);

        imgBtnUpload.setOnClickListener(this::uploadQr);
        btnUpload.setOnClickListener(this::uploadQr);
        imgBtnGenerate.setOnClickListener(this::generateQr);
        btnGenerate.setOnClickListener(this::generateQr);
        imgBtnUseNum.setOnClickListener(this::useNumber);
        btnUseNum.setOnClickListener(this::useNumber);

        btnBack.setOnClickListener(v -> finish());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(pvScanner.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    @SuppressWarnings("UnsafeOptInUsageError")
                    Image mediaImage = imageProxy.getImage();
                    if (mediaImage != null) {
                        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                        scanner.process(image)
                                .addOnSuccessListener(barcodes -> {
                                    for (Barcode barcode : barcodes) {
                                        String value = barcode.getRawValue();
                                        if (value != null) {
                                            runOnUiThread(() -> Toast.makeText(QRScanner.this, "QR Code: " + value, Toast.LENGTH_SHORT).show());
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("QRScanner", "Barcode scan failed", e))
                                .addOnCompleteListener(task -> imageProxy.close());
                    }
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("QRScanner", "Camera initialization failed", e);
            }

        }, ContextCompat.getMainExecutor(this));
    }

    private void uploadQr(View v) {
        // TODO
    }

    private void generateQr(View v) {
        // TODO
    }

    private void useNumber(View v) {
        Intent intent = new Intent(this, SendMoneyActivity.class);
        startActivity(intent);
    }
}