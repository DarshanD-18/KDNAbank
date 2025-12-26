package com.bank.banking.service;

import com.bank.banking.entity.Customer;
import com.bank.banking.entity.Ekyc;
import com.bank.banking.repository.EkycRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class EkycService {

    private final EkycRepository ekycRepository;

    @Value("${file.upload-dir:uploads/kyc}")
    private String uploadRoot;

    public EkycService(EkycRepository ekycRepository) {
        this.ekycRepository = ekycRepository;
    }

    public Ekyc submitEkyc(Customer customer, String aadhaarNumber, String panNumber,
                           MultipartFile aadhaarFile, MultipartFile panFile, MultipartFile addressProof) throws IOException {

        Ekyc ekyc = new Ekyc();
        ekyc.setCustomer(customer);
        ekyc.setAadhaarNumber(aadhaarNumber);
        ekyc.setPanNumber(panNumber);
        ekyc.setStatus("SUBMITTED");

        Path custDir = Path.of(uploadRoot, String.valueOf(customer.getId()));
        Files.createDirectories(custDir);

        if (aadhaarFile != null && !aadhaarFile.isEmpty()) {
            String fname = "aadhaar_" + System.currentTimeMillis() + "_" + aadhaarFile.getOriginalFilename();
            Path dest = custDir.resolve(fname);
            Files.copy(aadhaarFile.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            ekyc.setAadhaarFilePath(dest.toString());
        }

        if (panFile != null && !panFile.isEmpty()) {
            String fname = "pan_" + System.currentTimeMillis() + "_" + panFile.getOriginalFilename();
            Path dest = custDir.resolve(fname);
            Files.copy(panFile.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            ekyc.setPanFilePath(dest.toString());
        }

        if (addressProof != null && !addressProof.isEmpty()) {
            String fname = "addr_" + System.currentTimeMillis() + "_" + addressProof.getOriginalFilename();
            Path dest = custDir.resolve(fname);
            Files.copy(addressProof.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            ekyc.setAddressProofPath(dest.toString());
        }

        return ekycRepository.save(ekyc);
    }
}
