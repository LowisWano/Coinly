package com.example.coinly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coinly.db.Database;
import com.example.coinly.db.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class MyQRFragment extends Fragment {
    private Context ctx;

    private TextView fullNameTxt;
    private ImageView myQR;

    private String userId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_qr, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", null);

        // TODO: Generate QR code from phone number
        initViews(view);
        loadData();
    }

    private void initViews(View view) {
        fullNameTxt = view.findViewById(R.id.fullNameText);
        myQR = view.findViewById(R.id.myQR);

        view.findViewById(R.id.backButton).setOnClickListener(this::back);
    }

    private void loadData() {
        User.get(
                userId,
                User.Details.class,
                new Database.Data<User.Details>() {
                    @Override
                    public void onSuccess(User.Details data) {
                        fullNameTxt.setText(data.fullName.formatted());

                        Bitmap qrBitmap = generateQRCode(data.phoneNumber);

                        if (qrBitmap != null) {
                            myQR.setImageBitmap(qrBitmap);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("MyQRFragment", "Failed to load user data", e);
                        back(requireView());
                    }
                }
        );
    }

    public Bitmap generateQRCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();

        int size = Math.min(myQR.getWidth(), myQR.getHeight());

        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            Log.e("MyQRFragment", "Failed to generate QR code", e);
            return null;
        }
    }
    private void back(View view) {
        Navigation.findNavController(view).navigateUp();
    }
}