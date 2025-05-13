package com.example.coinly.db;

import com.google.firebase.firestore.FirebaseFirestore;

public class Database {
    public static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    public interface Data<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }

    public static class KeyAlreadyExists extends Exception {
        public KeyAlreadyExists(String message) {
            super(message);
        }
    }

    public static class DataNotFound extends Exception {
        public DataNotFound(String message) {
            super(message);
        }
    }
}
