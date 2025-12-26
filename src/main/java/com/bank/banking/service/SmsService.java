package com.bank.banking.service;

public interface SmsService {
    /**
     * Send an SMS text message to the supplied mobile number.
     * @param mobileNumber destination mobile number (with country code if required)
     * @param message message body
     * @return true if the message was accepted for delivery (best-effort)
     */
    boolean sendSms(String mobileNumber, String message);
}
