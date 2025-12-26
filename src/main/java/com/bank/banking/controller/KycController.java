package com.bank.banking.controller;

import com.bank.banking.entity.Customer;
import com.bank.banking.entity.KycDocument;
import com.bank.banking.repository.CustomerRepository;
import com.bank.banking.repository.KycDocumentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/official/ekyc")
public class KycController {

    private final CustomerRepository customerRepository;
    private final KycDocumentRepository kycDocumentRepository;

    private final Path uploadDir = Paths.get("uploads/kyc");

    public KycController(CustomerRepository customerRepository, KycDocumentRepository kycDocumentRepository) throws IOException {
        this.customerRepository = customerRepository;
        this.kycDocumentRepository = kycDocumentRepository;
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
    }

    @GetMapping
    public String showSearchForm() {
        return "official-ekyc";
    }

    @PostMapping("/search")
    public String searchCustomer(@RequestParam(required = false) Long customerId,
                                 @RequestParam(required = false) String mobileNumber,
                                 Model model) {

        Optional<Customer> opt = Optional.empty();
        if (customerId != null) opt = customerRepository.findById(customerId);
        if (opt.isEmpty() && mobileNumber != null && !mobileNumber.isBlank()) {
            opt = customerRepository.findByMobileNumber(mobileNumber);
        }

        if (opt.isEmpty()) {
            model.addAttribute("error", "Customer not found");
            return "official-ekyc";
        }

        model.addAttribute("customer", opt.get());
        return "official-ekyc-upload";
    }

    @PostMapping("/upload")
    public String uploadDocuments(@RequestParam Long customerId,
                                  @RequestParam MultipartFile aadhaar,
                                  @RequestParam MultipartFile pan,
                                  @RequestParam MultipartFile photo,
                                  @RequestParam MultipartFile addressProof,
                                  @RequestParam MultipartFile signature,
                                  Model model) throws IOException {

        Optional<Customer> opt = customerRepository.findById(customerId);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Customer not found");
            return "official-ekyc";
        }

        KycDocument doc = new KycDocument();
        doc.setCustomerId(customerId);
        doc.setStatus("PENDING");

        // helper to save file
        if (aadhaar != null && !aadhaar.isEmpty()) {
            String name = storeFile(aadhaar);
            doc.setAadhaarFile(name);
        }
        if (pan != null && !pan.isEmpty()) {
            String name = storeFile(pan);
            doc.setPanFile(name);
        }
        if (photo != null && !photo.isEmpty()) {
            String name = storeFile(photo);
            doc.setPhotoFile(name);
        }
        if (addressProof != null && !addressProof.isEmpty()) {
            String name = storeFile(addressProof);
            doc.setAddressProofFile(name);
        }
        if (signature != null && !signature.isEmpty()) {
            String name = storeFile(signature);
            doc.setSignatureFile(name);
        }

        kycDocumentRepository.save(doc);

        model.addAttribute("message", "KYC documents uploaded and pending approval");
        model.addAttribute("doc", doc);
        return "ekyc-success";
    }

    private String storeFile(MultipartFile file) throws IOException {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = System.currentTimeMillis() + "_" + original;
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target);
        return filename;
    }
}
