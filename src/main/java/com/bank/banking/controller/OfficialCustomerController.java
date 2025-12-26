package com.bank.banking.controller;

import com.bank.banking.entity.Customer;
import com.bank.banking.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

// @Controller
// Duplicate controller disabled (replaced by CustomerRegistrationController)
@RequestMapping("/official/customer-temp")
public class OfficialCustomerController {

    private final CustomerRepository customerRepository;

    public OfficialCustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "customer-register";
    }

    @PostMapping("/register")
    public String registerCustomer(@RequestParam String fullName,
                                   @RequestParam String mobileNumber,
                                   @RequestParam(required = false) String address,
                                   @RequestParam(required = false) String aadhaarNumber,
                                   @RequestParam(required = false) String panNumber,
                                   Model model) {

        // Prevent duplicate mobile registrations
        Optional<Customer> existing = customerRepository.findByMobileNumber(mobileNumber);
        if (existing.isPresent()) {
            model.addAttribute("message", "Customer with this mobile already exists");
            return "customer-register";
        }

        Customer c = new Customer();
        c.setName(fullName);
        c.setMobileNumber(mobileNumber);
        c.setKycCompleted(false);

        customerRepository.save(c);

        model.addAttribute("message", "Customer registered successfully");
        return "customer-register";
    }

    @GetMapping("/kyc")
    public String showKycForm() {
        return "customer-kyc";
    }

    @PostMapping("/kyc")
    public String completeKyc(@RequestParam String mobileNumber, Model model) {

        Optional<Customer> opt = customerRepository.findByMobileNumber(mobileNumber);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Customer not found");
            return "customer-kyc";
        }

        Customer c = opt.get();
        c.setKycCompleted(true);
        customerRepository.save(c);

        model.addAttribute("message", "KYC completed for " + c.getName());
        return "customer-kyc";
    }
}
