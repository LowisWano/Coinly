package com.example.coinly;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Util {
    public static String amountFormatter(Double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(amount);
    }

    public static String dateFormatter(GregorianCalendar date) {
        return (new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)).format(date.getTime());
    }
}
