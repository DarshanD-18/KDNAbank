package com.bank.banking.util;

public class AccountNumberGenerator {

    public static String generate12DigitAccountNumber() {
        long number = (long) (Math.random() * 1_000_000_000_000L);
        return String.format("%012d", number);
    }
}
