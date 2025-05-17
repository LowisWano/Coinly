package com.example.coinly.db;

import android.util.Log;

import com.example.coinly.Util;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Transaction implements Database.MapParser<Transaction> {
    public static enum Type {
        Transfer,
        Receive,
        Deposit,
    }

    public String id;
    public String senderId;
    public String receiveId;
    public double amount;
    public String name;
    public Type type;
    public GregorianCalendar date;

    public String formattedAmount() {
        return String.format(
                "%c Php %s",
                (this.type == Type.Transfer) ? '-' : '+',
                Util.amountFormatter(this.amount)
        );
    }

    public int amountColor() {
        return (this.type == Type.Transfer) ? 0xFFE91E63 : 0xFF4CAF50;
    }

    public Transaction withId(String id) {
        this.id = id;
        return this;
    }

    public Transaction withSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public Transaction withReceiveId(String receiveId) {
        this.receiveId = receiveId;
        return this;
    }

    public Transaction withAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public Transaction withName(String note) {
        this.name = note;
        return this;
    }

    public Transaction withType(Type type) {
        this.type = type;
        return this;
    }

    public Transaction withDate(GregorianCalendar date) {
        this.date = date;
        return this;
    }

    @Override
    public Transaction parser(Map<String, Object> map) {
        Object amount = map.get("amount");
        Object date = map.get("date");

        if (amount instanceof Long) {
            this.amount = ((Long) amount).doubleValue();
        } else if (amount instanceof Double) {
            this.amount = (Double) amount;
        }

        if (date instanceof Timestamp) {
            this.date = new GregorianCalendar();
            this.date.setTime(((Timestamp) date).toDate());
        }

        this.receiveId = (String) map.get("receiveId");
        this.senderId = (String) map.get("senderId");
        this.name = (String) map.get("name");

        return this;
    }

    public static void get(String userId, Database.Data<List<Transaction>> callback) {
        CollectionReference transactionsRef = Database.db().collection("transactions");

        Task<QuerySnapshot> sent = transactionsRef.whereEqualTo("senderId", userId).get();
        Task<QuerySnapshot> received = transactionsRef.whereEqualTo("receiveId", userId).get();

        Tasks.whenAllSuccess(sent, received)
                .addOnSuccessListener(results -> {
                    Set<String> seen = new HashSet<>();
                    List<Transaction> all = new ArrayList<>();

                    for (Object result : results) {
                        QuerySnapshot snapshot = (QuerySnapshot) result;
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            if (seen.add(doc.getId())) {
                                Map<String, Object> data = doc.getData();

                                if (data == null) {
                                    continue;
                                }

                                Transaction transaction = new Transaction().parser(data)
                                        .withId(doc.getId());

                                if (transaction.receiveId.equals(userId)) {
                                    transaction.withType(Type.Receive);
                                } else if (transaction.senderId.equals(userId)) {
                                    transaction.withType(Type.Transfer);
                                }

                                all.add(transaction);
                            }
                        }
                    }

                    callback.onSuccess(all);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void getFrom(String id, Database.Data<Transaction> callback) {
        Database.db().collection("transactions")
                .document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onFailure(new Database.DataNotFound("Transaction not found"));
                        return;
                    }

                    Map<String, Object> data = doc.getData();

                    if (data == null){
                        callback.onFailure(new Database.DataNotFound("Transaction not found"));
                        return;
                    }

                    callback.onSuccess(new Transaction().parser(data));
                })
                .addOnFailureListener(callback::onFailure);
    }
}
