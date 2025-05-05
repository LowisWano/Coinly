package com.example.coinly;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RequestTransactionHistoryActivity extends AppCompatActivity {
    private Spinner dateRangeSpinner;
    private TextView fromDateText;
    private TextView toDateText;
    private Calendar fromDate = Calendar.getInstance();
    private Calendar toDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

    private final String[] dateRanges = new String[]{
        "Select date range",
        "Last 7 days",
        "Last 30 days",
        "Last 90 days",
        "Custom range"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_transaction_history);

        setupToolbar();
        initializeViews();
        setupDateRangeSpinner();
        setupDatePickers();
        setupSubmitButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initializeViews() {
        dateRangeSpinner = findViewById(R.id.dateRangeSpinner);
        fromDateText = findViewById(R.id.fromDateText);
        toDateText = findViewById(R.id.toDateText);

        // Set current date as default
        fromDateText.setText(dateFormat.format(fromDate.getTime()));
        toDateText.setText(dateFormat.format(toDate.getTime()));
    }

    private void setupDateRangeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            dateRanges
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateRangeSpinner.setAdapter(adapter);

        dateRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1: // Last 7 days
                        setDateRange(7);
                        break;
                    case 2: // Last 30 days
                        setDateRange(30);
                        break;
                    case 3: // Last 90 days
                        setDateRange(90);
                        break;
                    case 4: // Custom range
                        // Reset dates to current date for custom range
                        fromDate = Calendar.getInstance();
                        toDate = Calendar.getInstance();
                        fromDateText.setText(dateFormat.format(fromDate.getTime()));
                        toDateText.setText(dateFormat.format(toDate.getTime()));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
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

    private void setupDatePickers() {
        fromDateText.setOnClickListener(v -> showDatePicker(fromDate, fromDateText));
        toDateText.setOnClickListener(v -> showDatePicker(toDate, toDateText));
    }

    private void showDatePicker(final Calendar date, final TextView dateText) {
        DatePickerDialog dialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                
                // Don't allow future dates
                Calendar today = Calendar.getInstance();
                if (selectedDate.after(today)) {
                    return;
                }
                
                // For "to" date, don't allow dates before "from" date
                if (dateText == toDateText && selectedDate.before(fromDate)) {
                    return;
                }
                
                // For "from" date, don't allow dates after "to" date
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
        
        // Set the maximum date to today
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        
        dialog.show();
    }

    private void setupSubmitButton() {
        findViewById(R.id.submitButton).setOnClickListener(v -> {
            showSuccessDialog();
        });
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_request_success);
        dialog.getWindow().setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.okayButton).setOnClickListener(v -> {
            dialog.dismiss();
            finish(); // Close the activity after success
        });

        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}