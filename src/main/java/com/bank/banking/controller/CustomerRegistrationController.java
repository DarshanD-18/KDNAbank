package com.bank.banking.controller;

import com.bank.banking.entity.Customer;
import com.bank.banking.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/official/customer")
public class CustomerRegistrationController {

    private final CustomerRepository customerRepository;

    public CustomerRegistrationController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // registration page may receive flash attributes (success/error)
        return "official-customer-register";
    }

    @PostMapping("/register")
    public String registerCustomer(@RequestParam String fullName,
                                   @RequestParam String dob,
                                   @RequestParam String gender,
                                   @RequestParam String mobileNumber,
                                   @RequestParam String email,
                                   @RequestParam String address,
                                   @RequestParam String aadhaarNumber,
                                   @RequestParam String panNumber,
                                   Model model,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        Optional<Customer> existing = customerRepository.findByMobileNumber(mobileNumber);
        if (existing.isPresent()) {
            model.addAttribute("message", "Customer with this mobile already exists");
            return "official-customer-register";
        }

        Customer c = new Customer();
        c.setName(fullName);
        if (dob != null && !dob.isBlank()) {
            c.setDob(LocalDate.parse(dob));
        }
        c.setGender(gender);
        c.setMobileNumber(mobileNumber);
        c.setEmail(email);
        c.setAddress(address);
        c.setAadhaarNumber(aadhaarNumber);
        c.setPanNumber(panNumber);
        c.setKycCompleted(false);
        c.setStatus("PENDING_APPROVAL");

        customerRepository.save(c);

        redirectAttributes.addFlashAttribute("success", "✅ Customer registered successfully and sent to Manager for approval.");
        return "redirect:/official/customer/register";
    }

}
