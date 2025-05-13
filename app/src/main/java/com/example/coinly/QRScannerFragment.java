package com.example.coinly;

import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRScannerFragment extends Fragment {
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int BUTTON_LAYOUT_DP = 20;

    private PreviewView pvScanner;
    private View scannerOverlay;
    private LinearLayout buttonLayout;

    private BarcodeScanner scanner;

    private ExecutorService cameraExecutor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrscanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pvScanner = view.findViewById(R.id.preview_view_scanner);
        scannerOverlay = view.findViewById(R.id.scanner_overlay_view);
        buttonLayout = view.findViewById(R.id.button_layout);

        scannerOverlay.post(this::initButtonLayoutPosition);

        initScanner();
        initButtons(view);
    }

    private void initScanner() {
        scanner = BarcodeScanning.getClient();
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
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

    private void initButtons(@NonNull View view) {
        ImageButton imgBtnUpload = view.findViewById(R.id.image_button_upload);
        Button btnUpload = view.findViewById(R.id.button_upload);
        ImageButton imgBtnGenerate = view.findViewById(R.id.image_button_generate_qr);
        Button btnGenerate = view.findViewById(R.id.button_generate_qr);
        ImageButton imgBtnUseNum = view.findViewById(R.id.image_button_use_number);
        Button btnUseNum = view.findViewById(R.id.button_use_number);

        imgBtnUpload.setOnClickListener(this::uploadQr);
        btnUpload.setOnClickListener(this::uploadQr);
        imgBtnGenerate.setOnClickListener(this::generateQr);
        btnGenerate.setOnClickListener(this::generateQr);
        imgBtnUseNum.setOnClickListener(this::useNumber);
        btnUseNum.setOnClickListener(this::useNumber);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

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
                                            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "QR Code: " + value, Toast.LENGTH_SHORT).show());
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

        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void uploadQr(View v) {
        // TODO
    }

    private void generateQr(View v) {
        // TODO
    }

    private void useNumber(View v) {
        // TODO
    }
}