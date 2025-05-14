package com.example.coinly.db;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class User {
    public static class Credentials {
        String email;
        String password;
        char[] pin = new char[4];

        public Credentials withEmail(String email) {
            this.email = email;
            return this;
        }

        public Credentials withPassword(String password) {
            this.password = password;
            return this;
        }

        public Credentials withPin(char[] pin) {
            this.pin = pin;
            return this;
        }
    }

    public static class Details {
        public static class FullName {
            String first;
            String last;
            char middleInitial;

            public FullName withFirst(String first) {
                this.first = first;
                return this;
            }

            public FullName withLast(String last) {
                this.last = last;
                return this;
            }

            public FullName withMiddleInitial(char middleInitial) {
                this.middleInitial = middleInitial;
                return this;
            }
        }

        String phoneNumber;
        FullName fullName;
        GregorianCalendar birthdate;

        public Details withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Details withFullName(FullName fullName) {
            this.fullName = fullName;
            return this;
        }

        public Details withBirthdate(GregorianCalendar birthdate) {
            this.birthdate = birthdate;
            return this;
        }
    }

    public static class Address {
        String street;
        String barangay;
        String city;
        String zipCode;

        public Address withStreet(String street) {
            this.street = street;
            return this;
        }

        public Address withBarangay(String barangay) {
            this.barangay = barangay;
            return this;
        }

        public Address withCity(String city) {
            this.city = city;
            return this;
        }

        public Address withZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }
    }

    public static class Wallet {
        float balance;

        public Wallet withBalance(float balance) {
            this.balance = balance;
            return this;
        }
    }

    public static class Savings {
        String name;
        float target;
        float balance;

        public Savings withName(String name) {
            this.name = name;
            return this;
        }

        public Savings withTarget(float target) {
            this.target = target;
            return this;
        }

        public Savings withBalance(float balance) {
            this.balance = balance;
            return this;
        }
    }

    public static void signUp(Credentials credentials, Details details, Database.Data<String> callback) {
        Map<String, Object> user = Map.of(
                "credentials", Map.of(
                        "email", credentials.email,
                        "password", credentials.password // TODO: Encrypt the password
                ),
                "details", Map.of(
                        "phoneNumber", details.phoneNumber,
                        "fullName", Map.of(
                                "first", details.fullName.first,
                                "last", details.fullName.last,
                                "middleInitial", Character.toString(details.fullName.middleInitial)
                        ),
                        "birthdate", details.birthdate
                )
        );

        FirebaseFirestore db = Database.db();

        db.collection("users")
                .whereEqualTo("credentials.email", credentials.email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        callback.onFailure(new Database.KeyAlreadyExists("Email already exists"));
                        return;
                    }

                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(doc -> callback.onSuccess(doc.getId()))
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void login(Credentials credentials, Database.Data<String> callback) {
        Database.db().collection("users")
                .whereEqualTo("credentials.email", credentials.email)
                .whereEqualTo("credentials.password", credentials.password) // TODO: Change to use decryption
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onFailure(new Database.DataNotFound("Email not found"));
                        return;
                    }

                    // TODO: Implement here the decryption
                    callback.onSuccess(querySnapshot.getDocuments().get(0).getId());
                })
                .addOnFailureListener(callback::onFailure);

    }

    public static void getBalance(Credentials credentials, Database.Balance callback) {
        Database.db().collection("users")
                .whereEqualTo("email", credentials.email)
                .whereEqualTo("password", credentials.password)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onFailure(new Database.DataNotFound("User not found"));
                        return;
                    }

                    // Get the balance from the first document that matches
                    Object balance = querySnapshot.getDocuments().get(0).get("balance");

                    if (balance == null) {
                        callback.onFailure(new Database.DataNotFound("Balance not found"));
                        return;
                    }

                    try {
                        // Convert the balance to float
                        float balanceValue = 0f;
                        if (balance instanceof Double) {
                            balanceValue = ((Double) balance).floatValue();
                        } else if (balance instanceof Long) {
                            balanceValue = ((Long) balance).floatValue();
                        } else if (balance instanceof String) {
                            balanceValue = Float.parseFloat((String) balance);
                        }

                        callback.onSuccess(balanceValue);
                    } catch (Exception e) {
                        callback.onFailure(new Exception("Error parsing balance: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void getTransactions(Credentials credentials, Database.Data<List<Map<String, Object>>> callback) {
        Database.db().collection("users")
                .whereEqualTo("email", credentials.email)
                .whereEqualTo("password", credentials.password)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onFailure(new Database.DataNotFound("User not found"));
                        return;
                    }

                    // Get the transactions from the first document that matches
                    List<Map<String, Object>> transactions = (List<Map<String, Object>>) querySnapshot.getDocuments().get(0).get("transactions");

                    if (transactions == null || transactions.isEmpty()) {
                        callback.onSuccess(new ArrayList<>()); // Return empty list if no transactions
                        return;
                    }

                    callback.onSuccess(transactions);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void setPin(String id, Credentials credentials, Database.Data<Void> callback) {
        DocumentReference docRef = Database.db().collection("users").document(id);

        docRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.exists()) {
                        callback.onFailure(new Database.DataNotFound("User not found"));
                        return;
                    }

                    docRef.update("credentials.pin", new String(credentials.pin))
                            .addOnSuccessListener(doc -> callback.onSuccess(null))
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void updateDetails(String id, Credentials credentials, Details details, Address address, Database.Data<Void> callback) {
        DocumentReference docRef = Database.db().collection("users").document(id);

        docRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.exists()) {
                        callback.onFailure(new Database.DataNotFound("User not found"));
                        return;
                    }

                    docRef.update(
                            "credentials.password", credentials.password,
                            "details.fullName.first", details.fullName.first,
                            "details.fullName.last", details.fullName.last,
                            "details.fullname.middleInitial", Character.toString(details.fullName.middleInitial),
                            "details.phoneNumber", details.phoneNumber,
                            "details.birthdate", details.birthdate.getTime(),
                            "address.street", address.street,
                            "address.barangay", address.barangay,
                            "address.city", address.city,
                            "address.zipCode", address.zipCode
                            )
                            .addOnSuccessListener(doc -> callback.onSuccess(null))
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }
}
