package com.example.coinly;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Objects;

public class HomeFragment extends Fragment {
    private Context ctx;

    private NavController homeNav;
    private final ImageView[] icons = new ImageView[4];

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        homeNav = ((NavHostFragment) Objects.requireNonNull(getChildFragmentManager().findFragmentById(R.id.nav_home))).getNavController();

        LinearLayout homeButton = view.findViewById(R.id.homeButton);
        LinearLayout pocketButton = view.findViewById(R.id.walletButton);
        LinearLayout qrButton = view.findViewById(R.id.qrButton);
        LinearLayout transactionsButton = view.findViewById(R.id.transactionsButton);
        LinearLayout profileButton = view.findViewById(R.id.profileButton);

        icons[0] = view.findViewById(R.id.homeIcon);
        icons[1] = view.findViewById(R.id.pocketsIcon);
        icons[2] = view.findViewById(R.id.transactionsIcon);
        icons[3] = view.findViewById(R.id.profileIcon);

        homeButton.setOnClickListener(this::home);
        pocketButton.setOnClickListener(this::pocket);
        qrButton.setOnClickListener(this::qrscanner);
        transactionsButton.setOnClickListener(this::transactions);
        profileButton.setOnClickListener(this::profile);

        homeNav.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();

            if (id == R.id.walletFragment) {
                setIcon(0);
            } else if (id == R.id.pocketFragment) {
                setIcon(1);
            } else if (id == R.id.transactionsFragment) {
                setIcon(2);
            } else if (id == R.id.profileFragment) {
                setIcon(3);
            } else {
                clearIcons();
            }
        });

    }

    private void clearIcons() {
        ColorStateList color = ContextCompat.getColorStateList(ctx, R.color.white);

        for (ImageView icon : icons) {
            icon.setImageTintList(color);
        }
    }

    private void setIcon(int index) {
        clearIcons();
        icons[index].setImageTintList(ContextCompat.getColorStateList(ctx, R.color.yellow_hard));
    }

    private void home(View v) {
        homeNav.navigate(R.id.walletFragment);
    }

    private void pocket(View v) {
        homeNav.navigate(R.id.pocketFragment);
    }

    private void qrscanner(View v) {
        homeNav.navigate(R.id.qrscannerFragment);
    }

    private void transactions(View v) {
        homeNav.navigate(R.id.transactionsFragment);
    }

    private void profile(View v) {
        homeNav.navigate(R.id.profileFragment);
    }
}