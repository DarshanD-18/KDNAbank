package com.bank.banking.controller;

import com.bank.banking.entity.Customer;
import com.bank.banking.entity.KycDocument;
import com.bank.banking.repository.CustomerRepository;
import com.bank.banking.repository.KycDocumentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/official/ekyc")
public class KycApprovalController {

    private final KycDocumentRepository kycDocumentRepository;
    private final CustomerRepository customerRepository;

    public KycApprovalController(KycDocumentRepository kycDocumentRepository, CustomerRepository customerRepository) {
        this.kycDocumentRepository = kycDocumentRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/pending")
    public String pendingList(Model model) {
        List<KycDocument> pending = kycDocumentRepository.findByStatus("PENDING");
        model.addAttribute("pending", pending);
        return "official-ekyc-pending";
    }

    @PostMapping("/approve")
    public String approve(@RequestParam Long kycId, @RequestParam String action) {
        Optional<KycDocument> opt = kycDocumentRepository.findById(kycId);
        if (opt.isPresent()) {
            KycDocument doc = opt.get();
            if ("approve".equalsIgnoreCase(action)) {
                doc.setStatus("APPROVED");
                kycDocumentRepository.save(doc);
                // set customer's kycCompleted true
                Optional<Customer> cOpt = customerRepository.findById(doc.getCustomerId());
                if (cOpt.isPresent()) {
                    Customer c = cOpt.get();
                    c.setKycCompleted(true);
                    customerRepository.save(c);
                }
            } else if ("reject".equalsIgnoreCase(action)) {
                doc.setStatus("REJECTED");
                kycDocumentRepository.save(doc);
            }
        }
        return "redirect:/official/ekyc/pending";
    }
}
