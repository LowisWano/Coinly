package com.example.coinly.db;

import java.util.GregorianCalendar;

public class Transaction {
    public static enum Type {
        Transfer,
        Receive,
        Deposit,
    }

    public String senderId;
    public String receiveId;
    public float amount;
    public String note;
    public Type type;
    public GregorianCalendar date;

    public Transaction withSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public Transaction withReceiveId(String receiveId) {
        this.receiveId = receiveId;
        return this;
    }

    public Transaction withAmount(float amount) {
        this.amount = amount;
        return this;
    }

    public Transaction withNote(String note) {
        this.note = note;
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
}
