package com.bank.banking.config;

import com.bank.banking.entity.User;
import com.bank.banking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, com.bank.banking.repository.CustomerRepository customerRepository) {
        return args -> {
            var opt = userRepository.findByUsername("manager");
            if (opt.isEmpty()) {
                User manager = new User();
                manager.setUsername("manager");
                manager.setPassword(new BCryptPasswordEncoder().encode("manager123"));
                manager.setRole("ROLE_MANAGER");
                manager.setApproved(true);
                userRepository.save(manager);
            } else {
                // ensure existing manager has correct role and is approved
                User manager = opt.get();
                boolean changed = false;
                if (!"ROLE_MANAGER".equals(manager.getRole())) {
                    manager.setRole("ROLE_MANAGER");
                    changed = true;
                }
                if (!manager.isApproved()) {
                    manager.setApproved(true);
                    changed = true;
                }
                if (changed) userRepository.save(manager);
            }
            // create a test customer and mark KYC completed for demonstration
            var custOpt = customerRepository.findByMobileNumber("9999999999");
            if (custOpt.isEmpty()) {
                com.bank.banking.entity.Customer c = new com.bank.banking.entity.Customer();
                c.setName("Test User");
                c.setMobileNumber("9999999999");
                c.setKycCompleted(true);
                customerRepository.save(c);
            }
            // ensure an official user exists for testing
            var optOff = userRepository.findByUsername("official");
            if (optOff.isEmpty()) {
                User official = new User();
                official.setUsername("official");
                official.setPassword(new BCryptPasswordEncoder().encode("official123"));
                official.setRole("ROLE_OFFICIAL");
                official.setApproved(true);
                userRepository.save(official);
            } else {
                User official = optOff.get();
                boolean changedOff = false;
                if (!"ROLE_OFFICIAL".equals(official.getRole())) {
                    official.setRole("ROLE_OFFICIAL");
                    changedOff = true;
                }
                if (!official.isApproved()) {
                    official.setApproved(true);
                    changedOff = true;
                }
                if (changedOff) userRepository.save(official);
            }
        };
    }
}
