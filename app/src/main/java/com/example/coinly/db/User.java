package com.example.coinly.db;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
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
        Date birthdate;

        public Details withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Details withFullName(FullName fullName) {
            this.fullName = fullName;
            return this;
        }

        public Details withBirthdate(Date birthdate) {
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

    public static void signUp(Credentials credentials, Details details, Database.ID callback) {
        Map<String, Object> user = Map.of(
                "credentials", Map.of(
                        "email", credentials.email,
                        "password", credentials.password, // TODO: Encrypt the password
                        "pin", credentials.pin
                ),
                "details", Map.of(
                        "phoneNumber", details.phoneNumber,
                        "fullName", Map.of(
                                "first", details.fullName.first,
                                "last", details.fullName.last,
                                "middleInitial", details.fullName.middleInitial
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

    public static void login(Credentials credentials, Database.ID callback) {
        Database.db().collection("users")
                .whereEqualTo("credentials.email", credentials.email)
                .whereEqualTo("credentials.password", credentials.password) // TODO: Change to use decryption
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
