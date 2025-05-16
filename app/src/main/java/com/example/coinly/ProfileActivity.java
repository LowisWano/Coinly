package com.example.coinly;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coinly.db.Database;
import com.example.coinly.db.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    LinearLayout currencyDropdown, languageDropdown, helpButton, logoutButton;
    TextView selectedCurrency, selectedLanguage;
    Button updateButton;

    String[] currencies = {"PHP", "USD", "EUR"};
    String[] languages = {"English", "Filipino", "Japanese"};
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        userId = getSharedPreferences("coinly", MODE_PRIVATE).getString("userId", null);

        currencyDropdown = findViewById(R.id.currency_dropdown_container);
        languageDropdown = findViewById(R.id.language_dropdown_container);
        helpButton = findViewById(R.id.help_button);
        logoutButton = findViewById(R.id.logout_button);
        updateButton = findViewById(R.id.update_button);

        selectedCurrency = findViewById(R.id.currency_selected_value);
        selectedLanguage = findViewById(R.id.language_selected_value);

        TextView profileName = findViewById(R.id.profile_name);
        TextView profilePhone = findViewById(R.id.profile_phone);
        TextView profileEmail = findViewById(R.id.profile_email);
        TextView profileBirthdate = findViewById(R.id.profile_birthdate);
        TextView profileAddress = findViewById(R.id.profile_address);

        loadUserProfile(userId, profileName, profilePhone, profileEmail, profileBirthdate, profileAddress);

//        currencyDropdown.setOnClickListener(v -> {
//            new AlertDialog.Builder(this)
//                    .setTitle("Choose Currency")
//                    .setItems(currencies, (dialog, which) -> {
//                        selectedCurrency.setText(currencies[which]);
//                    }).show();
//        });
//
//        languageDropdown.setOnClickListener(v -> {
//            new AlertDialog.Builder(this)
//                    .setTitle("Choose Language")
//                    .setItems(languages, (dialog, which) -> {
//                        selectedLanguage.setText(languages[which]);
//                    }).show();
//        });

        logoutButton.setOnClickListener(v -> {
            getSharedPreferences("coinly", MODE_PRIVATE)
                    .edit()
                    .remove("userId")
                    .apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        helpButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        updateButton.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
            View view = inflater.inflate(R.layout.dialog_update_profile, null);
            getUserInfo(userId, view);

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            EditText firstName = view.findViewById(R.id.edit_firstname);
            EditText middleInitial = view.findViewById(R.id.edit_middle_initial);
            EditText lastName = view.findViewById(R.id.edit_lastname);
            EditText phone = view.findViewById(R.id.edit_phone);
            EditText password = view.findViewById(R.id.edit_password);
            EditText confirmPassword = view.findViewById(R.id.edit_confirm_password);
            EditText birthdate = view.findViewById(R.id.edit_birthdate);

            EditText street = view.findViewById(R.id.edit_street);
            EditText barangay = view.findViewById(R.id.edit_barangay);
            EditText city = view.findViewById(R.id.edit_city);
            EditText zipcode = view.findViewById(R.id.edit_zipcode);
            Button editPinBtn = view.findViewById(R.id.edit_pin_btn);
            Button updateBtn = view.findViewById(R.id.update_btn);

            birthdate.setOnClickListener(v1 -> {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(ProfileActivity.this, (view1, year, month, dayOfMonth) -> {
                    String formatted = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
                            .format(new GregorianCalendar(year, month, dayOfMonth).getTime());
                    birthdate.setText(formatted);
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            });

            editPinBtn.setOnClickListener(v2 -> {
                Intent intent = new Intent(ProfileActivity.this, ChangePinActivity.class);
                startActivity(intent);
            });

            updateBtn.setOnClickListener(v2 -> {
                try {
                    // 1. Get input values
                    String fName = firstName.getText().toString().trim();
                    String mInitialStr = middleInitial.getText().toString().trim();
                    String lName = lastName.getText().toString().trim();
                    String phoneStr = phone.getText().toString().trim();
                    String pwd = password.getText().toString().trim();
                    String confirmPwd = confirmPassword.getText().toString().trim();

                    String streetStr = street.getText().toString().trim();
                    String barangayStr = barangay.getText().toString().trim();
                    String cityStr = city.getText().toString().trim();
                    String zipStr = zipcode.getText().toString().trim();
                    String birthdateStr = birthdate.getText().toString().trim();

//                  2. Validate fields (simple check — improve as needed)
                    if (fName.isEmpty() || lName.isEmpty() || phoneStr.isEmpty() ||
                            pwd.isEmpty() || confirmPwd.isEmpty() ||
                            birthdateStr.isEmpty()) {
                        Toast.makeText(ProfileActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!pwd.equals(confirmPwd)) {
                        Toast.makeText(ProfileActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 3. Convert birthdate string to GregorianCalendar
                    GregorianCalendar calendar;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
                        Date date = sdf.parse(birthdateStr);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    } catch (ParseException e) {
                        Toast.makeText(ProfileActivity.this, "Invalid birthdate format", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User.Credentials credentials = new User.Credentials()
                            .withPassword(pwd);

                    User.Details.FullName fullName = new User.Details.FullName()
                            .withFirst(fName)
                            .withLast(lName)
                            .withMiddleInitial(mInitialStr.isEmpty() ? ' ' : mInitialStr.charAt(0));

                    User.Details details = new User.Details()
                            .withFullName(fullName)
                            .withPhoneNumber(phoneStr)
                            .withBirthdate(calendar);

                    User.Address address = new User.Address()
                            .withStreet(streetStr)
                            .withBarangay(barangayStr)
                            .withCity(cityStr)
                            .withZipCode(zipStr);

                    User.updateDetails(userId, credentials, details, address, new Database.Data<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            loadUserProfile(userId, profileName, profilePhone, profileEmail, profileBirthdate, profileAddress);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ProfileActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout walletButton = findViewById(R.id.walletButton);
        LinearLayout qrButton = findViewById(R.id.qrButton);
        LinearLayout transactionsButton = findViewById(R.id.transactionsButton);
        LinearLayout profileButton = findViewById(R.id.profileButton);

        transactionsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionHistoryActivity.class));
            finish();
        });

        walletButton.setOnClickListener(v -> {
            // TODO: Navigate to Wallet screen
            startActivity(new Intent(this, PocketActivity.class));
            finish();
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(this, QRScanner.class));
            finish();
        });

        // Mark transactions button as selected
        profileButton.setSelected(true);

        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, WalletActivity.class));
            finish();
        });
    }

    private void getUserInfo(String userId, View view) {
        Database.db().collection("users").document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> fullNameMap = (Map<String, Object>) snapshot.get("details.fullName");
                    if (fullNameMap != null) {
                        ((EditText) view.findViewById(R.id.edit_firstname)).setText((String) fullNameMap.get("first"));
                        ((EditText) view.findViewById(R.id.edit_lastname)).setText((String) fullNameMap.get("last"));
                        Object mi = fullNameMap.get("middleInitial");
                        if (mi != null) {
                            ((EditText) view.findViewById(R.id.edit_middle_initial)).setText(mi.toString());
                        }
                    }

                    // !!! Might need to omit: Security risk
                    String password = (String) snapshot.get("credentials.password");
                    ((EditText) view.findViewById(R.id.edit_password)).setText(password);
                    ((EditText) view.findViewById(R.id.edit_confirm_password)).setText(password);

                    String phone = (String) snapshot.get("details.phoneNumber");
                    ((EditText) view.findViewById(R.id.edit_phone)).setText(phone);

                    Object birthdateObj = snapshot.get("details.birthdate");
                    if (birthdateObj instanceof com.google.firebase.Timestamp) {
                        com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) birthdateObj;
                        Date date = timestamp.toDate();
                        String formattedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(date);
                        ((EditText) view.findViewById(R.id.edit_birthdate)).setText(formattedDate);
                    }


                    // ADDRESS (if stored)
                    Map<String, Object> addressMap = (Map<String, Object>) snapshot.get("address");
                    if (addressMap != null) {
                        ((EditText) view.findViewById(R.id.edit_street)).setText((String) addressMap.get("street"));
                        ((EditText) view.findViewById(R.id.edit_barangay)).setText((String) addressMap.get("barangay"));
                        ((EditText) view.findViewById(R.id.edit_city)).setText((String) addressMap.get("city"));
                        ((EditText) view.findViewById(R.id.edit_zipcode)).setText((String) addressMap.get("zipCode"));
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserProfile(String userId, TextView name, TextView phone, TextView email, TextView birthdate, TextView address) {
        Database.db().collection("users").document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> fullNameMap = (Map<String, Object>) snapshot.get("details.fullName");
                    if (fullNameMap != null) {
                        String full = fullNameMap.get("first") + " " +
                                (fullNameMap.get("middleInitial") != null ? fullNameMap.get("middleInitial") + ". " : "") +
                                fullNameMap.get("last");
                        name.setText(full);
                    }

                    phone.setText((String) snapshot.get("details.phoneNumber"));
                    email.setText((String) snapshot.get("credentials.email"));

                    Object birthdateObj = snapshot.get("details.birthdate");
                    if (birthdateObj instanceof com.google.firebase.Timestamp) {
                        Date date = ((com.google.firebase.Timestamp) birthdateObj).toDate();
                        String formatted = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(date);
                        birthdate.setText(formatted);
                    }

                    Map<String, Object> addressMap = (Map<String, Object>) snapshot.get("address");
                    if (addressMap != null) {
                        String fullAddress = addressMap.get("street") + ", " +
                                addressMap.get("barangay") + ", " +
                                addressMap.get("city") + ", " +
                                addressMap.get("zipCode");
                        address.setText(fullAddress);
                    } else {
                        address.setText("No current address");
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}