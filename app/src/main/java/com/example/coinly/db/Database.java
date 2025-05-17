package com.example.coinly.db;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Database {
    public static FirebaseFirestore db() {
        return FirebaseFirestore.getInstance();
    }

    public interface Data<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }

    public interface MapParser<T> {
        T parser(Map<String, Object> map) throws Exception;
    }

    public interface Balance {
        void onSuccess(float balance);
        void onFailure(Exception e);
    }

    public static class KeyAlreadyExists extends RuntimeException {
        public KeyAlreadyExists(String message) {
            super(message);
        }
    }

    public static class DataNotFound extends RuntimeException {
        public DataNotFound(String message) {
            super(message);
        }
    }
}
