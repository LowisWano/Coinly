package com.example.coinly.db;

import com.google.firebase.firestore.FirebaseFirestore;

public class Database {
    public static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    public interface ID {
        void onSuccess(String id);
        void onFailure(Exception e);
    }

    public static class KeyAlreadyExists extends Exception {
        public KeyAlreadyExists(String message) {
            super(message);
        }
    }
}
