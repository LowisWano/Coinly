package com.example.coinly.db;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class User {
    public static class Credentials implements Database.MapParser<Credentials> {
        public String email;
        public String password;
        public char[] pin = new char[4];

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

        @Override
        public Credentials parser(Map<String, Object> map) throws Exception {
            Object rawData = map.get("credentials");

            if (!(rawData instanceof Map)) {
                throw new Database.DataNotFound("Credentials field not found");
            }

            Map<?, ?> data = (Map<?, ?>) rawData;

            this.email = (String) data.get("email");
            this.password = (String) data.get("password");
            this.pin = Optional.ofNullable((String) data.get("pin"))
                    .map(s -> (s.length() > 4) ? s.substring(0, 4) : s)
                    .orElse("")
                    .toCharArray();

            return this;
        }
    }

    public static class Details implements Database.MapParser<Details> {
        public static class FullName implements Database.MapParser<FullName> {
            public String first;
            public String last;
            public char middleInitial;

            public String formatted() {
                return String.format(
                        "%s %s%s",
                        this.first,
                        (this.middleInitial != '\0') ? this.middleInitial + ". " : "",
                        this.last
                );
            }

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

            @Override
            public FullName parser(Map<String, Object> map) {
                Object rawData = map.get("fullName");

                if (!(rawData instanceof Map)) {
                    return this;
                }

                Map<?, ?> data = (Map<?, ?>) rawData;

                this.first = (String) data.get("first");
                this.last = (String) data.get("last");
                this.middleInitial = Optional.ofNullable((String) data.get("middleInitial"))
                        .filter(s -> !s.isEmpty())
                        .map(s -> s.charAt(0))
                        .orElse('\0');

                return this;
            }
        }

        public String phoneNumber;
        public FullName fullName;
        public GregorianCalendar birthdate;

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

        @Override
        public Details parser(Map<String, Object> map) throws Exception {
            Object rawData = map.get("details");

            if (!(rawData instanceof Map<?, ?>)) {
                throw new Database.DataNotFound("Details field not found");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) rawData;

            this.phoneNumber = (String) data.get("phoneNumber");
            this.fullName = new FullName().parser(data);

            Object birthdate = data.get("birthdate");

            if (birthdate instanceof Timestamp) {
                this.birthdate = new GregorianCalendar();
                this.birthdate.setTime(((Timestamp) birthdate).toDate());
            }

            return this;
        }
    }

    public static class Address implements Database.MapParser<Address> {
        public String street;
        public String barangay;
        public String city;
        public String zipCode;

        public String formatted() {
            List<String> addressParts = new ArrayList<>();

            if (this.street != null && !this.street.isEmpty()) {
                addressParts.add(this.street);
            }

            if (this.barangay != null && !this.barangay.isEmpty()) {
                addressParts.add(this.barangay);
            }

            if (this.city != null && !this.city.isEmpty()) {
                addressParts.add(this.city);
            }

            if (this.zipCode != null && !this.zipCode.isEmpty()) {
                addressParts.add(this.zipCode);
            }

            return String.join(", ", addressParts);
        }

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

        @Override
        public Address parser(Map<String, Object> map) throws Exception {
            Object rawData = map.get("address");

            if (!(rawData instanceof Map)) {
                throw new Database.DataNotFound("Address field not found");
            }

            Map<?, ?> data = (Map<?, ?>) rawData;

            this.street = (String) data.get("street");
            this.barangay = (String) data.get("barangay");
            this.zipCode = (String) data.get("zipCode");
            this.city = (String) data.get("city");

            return this;
        }
    }

    public static class Wallet implements Database.MapParser<Wallet> {
        public double balance;

        public Wallet withBalance(double balance) {
            this.balance = balance;
            return this;
        }

        @Override
        public Wallet parser(Map<String, Object> map) throws Exception {
            Object rawData = map.get("wallet");

            if (!(rawData instanceof Map)) {
                throw new Database.DataNotFound("Wallet field not found");
            }

            Map<?, ?> data = (Map<?, ?>) rawData;
            Object balance = data.get("balance");

            if (balance instanceof Long) {
                this.balance = ((Long) balance).doubleValue();
            } else if (balance instanceof Double) {
                this.balance = (Double) balance;
            }

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
                        "birthdate", details.birthdate.getTime()
                ),
                "wallet", Map.of(
                        "balance", 0f
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

    private static void getSender(String id, Database.Data<DocumentSnapshot> callback) {
        FirebaseFirestore db = Database.db();
        db.collection("users").document(id).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        callback.onFailure(new Database.DataNotFound("Sender not found"));
                    } else {
                        callback.onSuccess(snapshot);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    private static void executeMoneyTransfer(
            DocumentReference senderRef,
            DocumentReference recipientRef,
            double amount,
            Database.Data<Transaction> callback
    ) {
        FirebaseFirestore db = Database.db();
        DocumentReference counterRef = db.collection("counters").document("transactions");

        db.runTransaction(transaction -> {
                    DocumentSnapshot senderSnap = transaction.get(senderRef);
                    DocumentSnapshot recipientSnap = transaction.get(recipientRef);
                    DocumentSnapshot counterSnap = transaction.get(counterRef);

                    float senderBalance = Objects.requireNonNull(senderSnap.getDouble("wallet.balance")).floatValue();
                    float recipientBalance = Objects.requireNonNull(recipientSnap.getDouble("wallet.balance")).floatValue();

                    if (senderBalance < amount) {
                        throw new IllegalArgumentException("Insufficient balance");
                    }

                    transaction.update(senderRef, "wallet.balance", senderBalance - amount);
                    transaction.update(recipientRef, "wallet.balance", recipientBalance + amount);

                    long nextId = counterSnap.getLong("value") + 1;
                    String id = Long.toString(nextId);
                    transaction.update(counterRef, "value", nextId);

                    Transaction txn = new Transaction()
                            .withId(id)
                            .withSenderId(senderRef.getId())
                            .withReceiveId(recipientRef.getId())
                            .withAmount(amount)
                            .withName("Send Money")
                            .withDate(new GregorianCalendar());

                    Map<String, Object> txnMap = new HashMap<>();
                    txnMap.put("senderId", txn.senderId);
                    txnMap.put("receiveId", txn.receiveId);
                    txnMap.put("amount", txn.amount);
                    txnMap.put("name", txn.name);
                    txnMap.put("date", txn.date.getTime());

                    DocumentReference txnRef = db.collection("transactions").document(id);
                    transaction.set(txnRef, txnMap);

                    return txn;
                })
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(callback::onFailure);
    }

    public static void sendMoney(String id, String toPhone, double amount, Database.Data<Transaction> callback) {
        FirebaseFirestore db = Database.db();
        DocumentReference senderRef = db.collection("users").document(id);

        getSender(id, new Database.Data<>() {
            @Override
            public void onSuccess(DocumentSnapshot senderSnapshot) {
                Map<String, Object> senderDetails = (Map<String, Object>) senderSnapshot.get("details");

                if (senderDetails == null) {
                    callback.onFailure(new IllegalArgumentException("Sender 'details' field is missing from the document."));
                    return;
                }

                String senderPhone = (String) senderDetails.get("phoneNumber");

                if (senderPhone != null && senderPhone.equals(toPhone)) {
                    callback.onFailure(new IllegalArgumentException("Cannot send money to yourself"));
                    return;
                }

                db.collection("users")
                        .whereEqualTo("details.phoneNumber", toPhone)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (querySnapshot.isEmpty()) {
                                callback.onFailure(new Database.DataNotFound("Recipient not found"));
                                return;
                            }

                            DocumentSnapshot recipientSnapshot = querySnapshot.getDocuments().get(0);
                            DocumentReference recipientRef = recipientSnapshot.getReference();

                            executeMoneyTransfer(senderRef, recipientRef, amount, callback);
                        })
                        .addOnFailureListener(callback::onFailure);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static <T extends Database.MapParser<T>> void get(String id, Class<T> clazz, Database.Data<T> callback) {
        Database.db().collection("users")
                .document(id)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.exists()) {
                        callback.onFailure(new Database.DataNotFound("User not found"));
                        return;
                    }

                    Map<String, Object> data = querySnapshot.getData();

                    if (data == null) {
                        callback.onFailure(new Database.DataNotFound("User's data is empty"));
                        return;
                    }

                    try {
                        T instance = clazz.newInstance();

                        callback.onSuccess(instance.parser(data));
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static <T extends Database.MapParser<T>> void getFromPhoneNumber(String phoneNumber, Class<T> clazz, Database.Data<T> callback) {
        Database.db().collection("users")
                .whereEqualTo("details.phoneNumber", phoneNumber)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onFailure(new Database.DataNotFound("User with phone number not found"));
                        return;
                    }

                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                    Map<String, Object> data = doc.getData();

                    if (data == null) {
                        callback.onFailure(new Database.DataNotFound("User with phone number not found"));
                        return;
                    }

                    try {
                        T instance = clazz.newInstance();

                        callback.onSuccess(instance.parser(data));
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}
