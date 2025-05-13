package com.example.coinly.db;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Map;

public class User {
    public static class Credentials {
        String email;
        String password;
        char[] pin = new char[4];
    }

    public static class Details {
        public static class FullName {
            String first;
            String last;
            char middleInitial;
        }

        FullName fullName;
        Date birthdate;
    }

    public static class Address {
        String street;
        String barangay;
        String city;
        String zipCode;
    }

    public static class Wallet {
        float balance;
    }

    public static class Savings {
        String name;
        float target;
        float balance;
    }

    public static void signUp(Credentials credentials, Database.ID callback) {
        Map<String, Object> user = Map.of(
                "email", credentials.email,
                "password", credentials.password, // TODO: Encrypt the password
                "pin", credentials.pin
        );

        FirebaseFirestore db = Database.db();

        db.collection("users")
                .whereEqualTo("email", credentials.email)
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

    public static void login(Credentials credentials, Database.ID callback) {
        Database.db().collection("users")
                .whereEqualTo("email", credentials.email)
                .whereEqualTo("password", credentials.password) // TODO: Change to use decryption
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onFailure(new Database.DataNotFound("Email not found"));
                    }

                    // TODO: Implement here the decryption
                    callback.onSuccess(querySnapshot.getDocuments().get(0).getId());
                })
                .addOnFailureListener(callback::onFailure);

    }
}
