package com.bank.banking.security;

import com.bank.banking.entity.OfficialRequest;
import com.bank.banking.entity.User;
import com.bank.banking.repository.OfficialRequestRepository;
import com.bank.banking.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;   // ✅ ADD THIS



@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final OfficialRequestRepository officialRequestRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    OfficialRequestRepository officialRequestRepository) {
        this.userRepository = userRepository;
        this.officialRequestRepository = officialRequestRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // 🔹 Fetch user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // 🔹 IF OFFICIAL → CHECK APPROVAL
        if (user.getRole().equals("ROLE_OFFICIAL")) {
            // If the User record is already marked approved, trust it.
            if (!user.isApproved()) {
                // Fallback: check the OfficialRequest record if present.
                var optReq = officialRequestRepository.findByUsername(username);
                if (optReq.isPresent()) {
                    if (!"APPROVED".equals(optReq.get().getStatus())) {
                        throw new RuntimeException("❌ Official not approved by manager");
                    }
                } else {
                    // No approval information available — deny access with clear message
                    throw new RuntimeException("Approval request not found for official user");
                }
            }
        }

        // 🔹 RETURN SPRING SECURITY USER
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
