package com.bank.banking.controller;

import com.bank.banking.entity.Customer;
import com.bank.banking.entity.Ekyc;
import com.bank.banking.repository.CustomerRepository;
import com.bank.banking.repository.EkycRepository;
import com.bank.banking.service.EkycService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/official/ekyc/new")
public class EkycController {

    private final CustomerRepository customerRepository;
    private final EkycService ekycService;
    private final EkycRepository ekycRepository;

    public EkycController(CustomerRepository customerRepository, EkycService ekycService, EkycRepository ekycRepository) {
        this.customerRepository = customerRepository;
        this.ekycService = ekycService;
        this.ekycRepository = ekycRepository;
    }

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("customers", customerRepository.findByStatus("APPROVED"));
        return "official-ekyc-new"; // separate view for the alternate eKYC flow
    }

    @PostMapping
    public String submitEkyc(@RequestParam Long customerId,
                             @RequestParam String aadhaarNumber,
                             @RequestParam String panNumber,
                             @RequestParam MultipartFile aadhaarFile,
                             @RequestParam MultipartFile panFile,
                             @RequestParam MultipartFile addressProof,
                             RedirectAttributes redirectAttributes) {

        var opt = customerRepository.findById(customerId);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Selected customer not found.");
            return "redirect:/official/ekyc";
        }

        Customer cust = opt.get();

        try {
            Ekyc ekyc = ekycService.submitEkyc(cust, aadhaarNumber, panNumber, aadhaarFile, panFile, addressProof);
            redirectAttributes.addFlashAttribute("success", "eKYC submitted successfully and sent to Manager for verification.");
        } catch (IOException ex) {
            redirectAttributes.addFlashAttribute("error", "Failed to save eKYC documents: " + ex.getMessage());
        }

        return "redirect:/official/ekyc/new";
    }
}
