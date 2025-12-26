package com.bank.banking.controller;

import com.bank.banking.entity.OfficialRequest;
import com.bank.banking.repository.OfficialRequestRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/official/request")
public class OfficialRequestController {

    private final OfficialRequestRepository repository;
    private final BCryptPasswordEncoder encoder;

    public OfficialRequestController(OfficialRequestRepository repository,
                                     BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @GetMapping
    public String requestForm() {
        return "official-request";
    }

    @PostMapping
    public String submit(@RequestParam String username,
                         @RequestParam String password,
                         Model model) {

        OfficialRequest req = new OfficialRequest();
        req.setUsername(username);
        req.setPassword(encoder.encode(password));
        req.setStatus("PENDING");

        repository.save(req);

        model.addAttribute("msg", "Request sent to manager");
        return "official-request";
    }
}
