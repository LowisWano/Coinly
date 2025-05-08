package com.example.coinly.db;

public class Transaction {
    public static enum Type {
        Transfer,
        Receive,
        Deposit,
    }

    String senderId;
    String receiveId;
    float amount;
    String note;
}
