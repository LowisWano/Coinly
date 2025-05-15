package com.example.coinly.db;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

                        Pocket pocket = new Pocket().parser(data);
                        pocket.id = doc.getId();
                        pockets.add(pocket);
                    }

                    callback.onSuccess(pockets);
                })
                .addOnFailureListener(callback::onFailure);
    }
}
