package com.example.coinly;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coinly.db.Database;
import com.example.coinly.db.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RequestTransactionHistoryFragment extends Fragment implements Database.Data<User.Credentials> {
    private Context ctx;

    private Spinner dateRangeSpinner;
    private TextView emailText;
    private TextView fromDateText;
    private TextView toDateText;
    private Button submitButton;
    private Calendar fromDate = Calendar.getInstance();
    private Calendar toDate = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

    private final String[] dateRanges = new String[]{
        "Select date range",
        "Last 7 days",
        "Last 30 days",
        "Last 90 days",
        "Custom range"
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_transaction_history, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        setupDateRangeSpinner();
        setupDatePickers();
    }

    private void initViews(View view) {
        dateRangeSpinner = view.findViewById(R.id.dateRangeSpinner);
        fromDateText = view.findViewById(R.id.fromDateText);
        toDateText = view.findViewById(R.id.toDateText);
        submitButton = view.findViewById(R.id.submitButton);
        emailText = view.findViewById(R.id.emailText);

        fromDateText.setText(dateFormat.format(fromDate.getTime()));
        toDateText.setText(dateFormat.format(toDate.getTime()));

        submitButton.setOnClickListener(v -> showSuccessDialog());
        view.findViewById(R.id.backButton).setOnClickListener(this::back);

        User.get(
                ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", ""),
                User.Credentials.class,
                this
        );
    }

    private void setupDateRangeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            ctx,
            android.R.layout.simple_spinner_item,
            dateRanges
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateRangeSpinner.setAdapter(adapter);

        dateRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                submitButton.setEnabled(position != 0);

                switch (position) {
                    case 1:
                        setDateRange(7);
                        disableDatePickers();
                        break;
                    case 2:
                        setDateRange(30);
                        disableDatePickers();
                        break;
                    case 3:
                        setDateRange(90);
                        disableDatePickers();
                        break;
                    case 4:
                        enableDatePickers();
                        break;
                    default:
                        disableDatePickers();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                disableDatePickers();
            }
        });
    }

    private void setDateRange(int days) {
        toDate = Calendar.getInstance();
        fromDate = Calendar.getInstance();
        fromDate.add(Calendar.DAY_OF_MONTH, -days);
        
        fromDateText.setText(dateFormat.format(fromDate.getTime()));
        toDateText.setText(dateFormat.format(toDate.getTime()));
    }

    private void enableDatePickers() {
        fromDateText.setEnabled(true);
        toDateText.setEnabled(true);
        fromDateText.setAlpha(1.0f);
        toDateText.setAlpha(1.0f);
    }

    private void disableDatePickers() {
        fromDateText.setEnabled(false);
        toDateText.setEnabled(false);
        fromDateText.setAlpha(0.5f);
        toDateText.setAlpha(0.5f);
    }

    private void setupDatePickers() {
        fromDateText.setOnClickListener(v -> showDatePicker(fromDate, fromDateText));
        toDateText.setOnClickListener(v -> showDatePicker(toDate, toDateText));
    }

    private void showDatePicker(final Calendar date, final TextView dateText) {
        DatePickerDialog dialog = new DatePickerDialog(
            ctx,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                
                Calendar today = Calendar.getInstance();
                if (selectedDate.after(today)) {
                    return;
                }
                
                if (dateText == toDateText && selectedDate.before(fromDate)) {
                    return;
                }
                
                if (dateText == fromDateText && selectedDate.after(toDate)) {
                    toDate.setTime(selectedDate.getTime());
                    toDateText.setText(dateFormat.format(toDate.getTime()));
                }
                
                date.set(year, month, dayOfMonth);
                dateText.setText(dateFormat.format(date.getTime()));
            },
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH)
        );
        
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        
        if (dateText == toDateText) {
            dialog.getDatePicker().setMinDate(fromDate.getTimeInMillis());
        }
        
        dialog.show();
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.dialog_request_success);
        dialog.getWindow().setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.okayButton).setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        dialog.show();
    }

    private void back(View view) {
        Navigation.findNavController(view).navigateUp();
    }

    @Override
    public void onSuccess(User.Credentials data) {
        emailText.setText(data.email);
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("Credentials", "unable to get user credentials", e);
        Navigation.findNavController(requireView()).navigateUp();
    }
}