package com.example.coinly;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.coinly.db.Database;
import com.example.coinly.db.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    private Context ctx;

    private LinearLayout helpButton, logoutButton;
    private TextView profileName, profilePhone, profileEmail, profileBirthdate, profileAddress;
    private Button updateButton;

    private String userId;

    private User.Details userDetails;
    private User.Address userAddress;
    private User.Credentials userCredentials;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userId = ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE).getString("userId", null);

        initViews(view);
        initButtons();
        loadUserProfile();
    }

    private void initViews(View view) {
        helpButton = view.findViewById(R.id.help_button);
        logoutButton = view.findViewById(R.id.logout_button);
        updateButton = view.findViewById(R.id.update_button);

        profileName = view.findViewById(R.id.profile_name);
        profilePhone = view.findViewById(R.id.profile_phone);
        profileEmail = view.findViewById(R.id.profile_email);
        profileBirthdate = view.findViewById(R.id.profile_birthdate);
        profileAddress = view.findViewById(R.id.profile_address);
    }

    private void initButtons() {
        logoutButton.setOnClickListener(v -> ctx.getSharedPreferences("coinly", Context.MODE_PRIVATE)
                .edit()
                .remove("userId")
                .apply()
        );

        helpButton.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(ProfileFragmentDirections.actionProfileFragmentToHelpFragment());
        });

        updateButton.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View view = inflater.inflate(R.layout.dialog_update_profile, null);
            getUserInfo(view);

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
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
                new DatePickerDialog(ctx, (view1, year, month, dayOfMonth) -> {
                    String formatted = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
                            .format(new GregorianCalendar(year, month, dayOfMonth).getTime());
                    birthdate.setText(formatted);
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            });

            editPinBtn.setOnClickListener(v3 -> {
                Navigation.findNavController(v)
                        .navigate(ProfileFragmentDirections.actionProfileFragmentToChangePinFragment());
                dialog.dismiss();
            });

            updateBtn.setOnClickListener(v2 -> {
                try {
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

                    if (fName.isEmpty() || lName.isEmpty() || phoneStr.isEmpty() ||
                            pwd.isEmpty() || confirmPwd.isEmpty() ||
                            birthdateStr.isEmpty()) {
                        Toast.makeText(ctx, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!pwd.equals(confirmPwd)) {
                        Toast.makeText(ctx, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GregorianCalendar calendar;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
                        Date date = sdf.parse(birthdateStr);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    } catch (ParseException e) {
                        Toast.makeText(ctx, "Invalid birthdate format", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ctx, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadUserProfile();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ctx, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (Exception e) {
                    Toast.makeText(ctx, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void getUserInfo(View view) {
        ((TextView) view.findViewById(R.id.edit_firstname)).setText(userDetails.fullName.first);
        ((TextView) view.findViewById(R.id.edit_lastname)).setText(userDetails.fullName.last);
        ((TextView) view.findViewById(R.id.edit_middle_initial)).setText(Character.toString(userDetails.fullName.middleInitial));
        ((TextView) view.findViewById(R.id.edit_phone)).setText(userDetails.phoneNumber);
        ((TextView) view.findViewById(R.id.edit_birthdate)).setText(Util.dateFormatter(userDetails.birthdate));

        ((TextView) view.findViewById(R.id.edit_street)).setText(userAddress.street);
        ((TextView) view.findViewById(R.id.edit_barangay)).setText(userAddress.barangay);
        ((TextView) view.findViewById(R.id.edit_city)).setText(userAddress.city);
        ((TextView) view.findViewById(R.id.edit_zipcode)).setText(userAddress.zipCode);

        // HACK: Might need to omit: Security risk
        ((TextView) view.findViewById(R.id.edit_password)).setText(userCredentials.password);
        ((TextView) view.findViewById(R.id.edit_confirm_password)).setText(userCredentials.password);
    }

    private void loadUserProfile() {
        User.get(
                userId,
                User.Details.class,
                new Database.Data<User.Details>() {
                    @Override
                    public void onSuccess(User.Details data) {
                        userDetails = data;

                        profileName.setText(data.fullName.formatted());
                        profilePhone.setText(data.phoneNumber);
                        profileBirthdate.setText(Util.dateFormatter(data.birthdate));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("ProfileFragment", "Failed to load user profile: " + e.getMessage());
                        back(requireView());
                    }
                }
        );

        User.get(
                userId,
                User.Credentials.class,
                new Database.Data<User.Credentials>() {
                    @Override
                    public void onSuccess(User.Credentials data) {
                        userCredentials = data;

                        profileEmail.setText(data.email);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("ProfileFragment", "Failed to load user profile: " + e.getMessage());
                        back(requireView());
                    }
                }
        );

        User.get(
                userId,
                User.Address.class,
                new Database.Data<User.Address>() {
                    @Override
                    public void onSuccess(User.Address data) {
                        userAddress = data;

                        if (data.street.isEmpty() &&
                                data.barangay.isEmpty() &&
                                data.zipCode.isEmpty() &&
                                data.city.isEmpty()) {
                            profileAddress.setText("No current address");
                        } else {
                            profileAddress.setText(data.formatted());
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (!(e instanceof Database.DataNotFound)) {
                            Log.e("ProfileFragment", "Failed to load user profile: " + e.getMessage());
                            back(requireView());
                        } else {
                            profileAddress.setText("No current address");
                        }
                    }
                }
        );
    }

    private void back(View view) {
        Navigation.findNavController(view).navigateUp();
    }
}