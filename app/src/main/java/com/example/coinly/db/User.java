package com.example.coinly.db;

import android.content.Context;

import java.util.Date;
import java.util.HashMap;

public class User {
    public static class Credentials {
        String phoneNumber;
        String passwordHash;
        char[] pin = new char[4];
    }

    public static class Details {
        public static class FullName {
            String first;
            String last;
            char middleInitial;
        }

        String email;
        FullName fullName;
        Date birthdate;
    }

    public static class Address {
        String street;
        String barangay;
        String city;
        String zipCode;
    }

    public static class Wallet {
        float balance;
    }

    public static class Savings {
        String name;
        float target;
        float balance;
    }
}
