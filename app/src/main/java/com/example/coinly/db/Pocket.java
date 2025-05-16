package com.example.coinly.db;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Pocket implements Database.MapParser<Pocket> {
    public String id;
    public String userId;
    public String name;
    public double target;
    public double balance;
    public boolean locked;

    public int percent() {
        return (int) (balance / target * 100);
    }

    public Pocket withId(String id) {
        this.id = id;
        return this;
    }

    public Pocket withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Pocket withName(String name) {
        this.name = name;
        return this;
    }

    public Pocket withTarget(double target) {
        this.target = target;
        return this;
    }

    public Pocket withBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public Pocket withLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    @Override
    public Pocket parser(Map<String, Object> map) {
        Object target = map.get("target");
        Object balance = map.get("balance");

        if (target instanceof Long) {
            this.target = ((Long) target).doubleValue();
        } else if (target instanceof Double) {
            this.target = (Double) target;
        }

        if (balance instanceof Long) {
            this.balance = ((Long) balance).doubleValue();
        } else if (balance instanceof Double) {
            this.balance = (Double) balance;
        }

        this.userId = (String) map.get("userId");
        this.name = (String) map.get("name");
        this.locked = (Boolean) map.get("locked");

        return this;
    }

    public static void add(String userId, Pocket pocket, Database.Data<Void> callback) {
        FirebaseFirestore db = Database.db();

        DocumentReference userRef = db.collection("users").document(userId);
        CollectionReference pocketRef = db.collection("pockets");

        db.runTransaction(transaction -> {
            DocumentSnapshot userSnap = transaction.get(userRef);
            Map<String, Object> walletMap = (Map<String, Object>) userSnap.get("wallet");

            if (!(walletMap != null && walletMap.get("balance") instanceof Number)) {
                throw new Database.DataNotFound("User's wallet not found");
            }

            double walletBalance = ((Number) Objects.requireNonNull(walletMap.get("balance"))).doubleValue();

            if (walletBalance < pocket.balance) {
                throw new IllegalArgumentException("Insufficient wallet balance");
            }

            double updatedBalance = walletBalance - pocket.balance;
            transaction.update(userRef, "wallet.balance", updatedBalance);

            Map<String, Object> pocketData = Map.of(
                    "userId", userId,
                    "name", pocket.name,
                    "balance", pocket.balance,
                    "target", pocket.target,
                    "locked", false
            );

            transaction.set(pocketRef.document(), pocketData);

            return null;
        })
                .addOnSuccessListener(r -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public static void withdraw(String userId, String pocketId, double amount, Database.Data<Pocket> callback) {
        FirebaseFirestore db = Database.db();

        DocumentReference userRef = db.collection("users").document(userId);
        DocumentReference pocketRef = db.collection("pockets").document(pocketId);

        db.runTransaction(transaction -> {
            DocumentSnapshot pocketSnapshot = transaction.get(pocketRef);

            if (!pocketSnapshot.exists()) {
                throw new Database.DataNotFound("Pocket not found");
            }

            Double pocketBalance = pocketSnapshot.getDouble("balance");

            if (pocketBalance == null || pocketBalance <= 0 || pocketBalance < amount) {
                throw new IllegalArgumentException("Insufficient pocket balance");
            }

            DocumentSnapshot userSnapshot = transaction.get(userRef);

            if (!userSnapshot.exists()) {
                throw new Database.DataNotFound("User not found");
            }

            Map<String, Object> walletMap = (Map<String, Object>) userSnapshot.get("wallet");

            double walletBalance = 0.0;

            if (walletMap != null && walletMap.get("balance") instanceof Number) {
                walletBalance = ((Number) Objects.requireNonNull(walletMap.get("balance"))).doubleValue();
            }

            transaction.update(pocketRef, "balance", pocketBalance - amount);
            transaction.update(userRef, "wallet.balance", walletBalance + amount);

            return null;
        }).addOnSuccessListener(r -> {
            pocketRef.get().addOnSuccessListener(updatedSnapshot -> {
                if (updatedSnapshot.exists()) {
                    Pocket data = new Pocket()
                            .withId(updatedSnapshot.getId())
                            .parser(updatedSnapshot.getData());

                    callback.onSuccess(data);
                } else {
                    callback.onFailure(new Exception("Failed to fetch updated pocket."));
                }
            }).addOnFailureListener(callback::onFailure);
        }).addOnFailureListener(callback::onFailure);
    }

    public static void deposit(String userId, String pocketId, double amount, Database.Data<Pocket> callback) {
        FirebaseFirestore db = Database.db();

        DocumentReference userRef = db.collection("pockets").document(pocketId);
        DocumentReference pocketRef = db.collection("pockets").document(userId);

        db.runTransaction(transaction -> {
            DocumentSnapshot userSnapshot = transaction.get(userRef);
            if (!userSnapshot.exists()) {
                throw new Database.DataNotFound("User not found");
            }

            Map<String, Object> walletMap = (Map<String, Object>) userSnapshot.get("wallet");

            double walletBalance = 0.0;
            if (walletMap != null && walletMap.get("balance") instanceof Number) {
                walletBalance = ((Number) Objects.requireNonNull(walletMap.get("balance"))).doubleValue();
            }

            if (walletBalance < amount || walletBalance <= 0) {
                throw new IllegalArgumentException("Insufficient wallet balance");
            }

            DocumentSnapshot pocketSnapshot = transaction.get(pocketRef);
            if (!pocketSnapshot.exists()) {
                throw new Database.DataNotFound("Pocket not found");
            }

            Double pocketBalance = pocketSnapshot.getDouble("balance");
            if (pocketBalance == null) pocketBalance = 0.0;

            transaction.update(userRef, "wallet.balance", walletBalance - amount);
            transaction.update(pocketRef, "balance", pocketBalance + amount);

            return null;
        }).addOnSuccessListener(r -> {
            pocketRef.get().addOnSuccessListener(updatedSnapshot -> {
                if (updatedSnapshot.exists()) {
                    Pocket data = new Pocket()
                            .withId(updatedSnapshot.getId())
                            .parser(updatedSnapshot.getData());

                    callback.onSuccess(data);
                } else {
                    callback.onFailure(new Database.DataNotFound("Pocket not found"));
                }
            }).addOnFailureListener(callback::onFailure);
        }).addOnFailureListener(callback::onFailure);
    }

    public static void get(String userId, Database.Data<List<Pocket>> callback) {
        Database.db().collection("pockets")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(results -> {
                    List<Pocket> pockets = new ArrayList<>();

                    for (DocumentSnapshot doc : results.getDocuments()) {
                        Map<String, Object> data = doc.getData();

                        if (data == null) {
                            continue;
                        }

                        pockets.add(new Pocket().parser(data).withId(doc.getId()));
                    }

                    callback.onSuccess(pockets);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void getFrom(String id, Database.Data<Pocket> callback) {
        Database.db().collection("pockets")
                .document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onFailure(new Database.DataNotFound("Pocket not found"));
                        return;
                    }

                    Map<String, Object> data = doc.getData();

                    if (data == null){
                        callback.onFailure(new Database.DataNotFound("Pocket not found"));
                        return;
                    }

                    callback.onSuccess(new Pocket().parser(data).withId(doc.getId()));
                })
                .addOnFailureListener(callback::onFailure);
    }
}
