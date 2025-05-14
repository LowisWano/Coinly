package com.example.coinly;

public class Transaction {
    private String title;
    private String date;
    private double amount;
    private String refNumber;
    private String sender;
    private String receiver;

    public Transaction(String title, String date, double amount) {
        this(title, date, amount, "123123", "Unknown", "Unknown");
    }

    public Transaction(String title, String date, double amount, String refNumber, String sender, String receiver) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.refNumber = refNumber;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getFormattedAmount() {
        return (amount >= 0 ? "+ " : "- ") + "Php " + String.format("%.2f", Math.abs(amount));
    }

    public int getAmountColor() {
        return amount >= 0 ? 0xFF4CAF50 : 0xFFE91E63; // Green for positive, Pink for negative
    }
}