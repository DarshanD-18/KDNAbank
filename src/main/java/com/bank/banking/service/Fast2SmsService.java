package com.bank.banking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class Fast2SmsService implements SmsService {

    @Value("${fast2sms.apiKey:}")
    private String apiKey;

    @Value("${fast2sms.url:https://www.fast2sms.com/dev/bulkV2}")
    private String apiUrl;

    private final RestTemplate rest = new RestTemplate();

    @Override
    public boolean sendSms(String mobileNumber, String message) {
        if (apiKey == null || apiKey.isBlank()) {
            // fallback to console logging for environments without configured API key
            System.out.println("[SMS - simulated] to=" + mobileNumber + " msg=" + message);
            return true;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("authorization", apiKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("sender_id", "FSTSMS");
        body.add("message", message);
        body.add("language", "english");
        body.add("route", "v3");
        body.add("numbers", mobileNumber);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> resp = rest.postForEntity(apiUrl, request, String.class);
            return resp.getStatusCode().is2xxSuccessful();
        } catch (Exception ex) {
            System.err.println("Failed to send SMS via Fast2SMS: " + ex.getMessage());
            return false;
        }
    }
}
