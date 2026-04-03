package com.sankalp.financedashboard.config;

import com.sankalp.financedashboard.entity.Role;
import com.sankalp.financedashboard.entity.User;
import com.sankalp.financedashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds default ADMIN and ANALYST users for local testing.
 * Only active under the "dev" profile.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUserIfMissing("admin@gmail.com", "Admin", "User", "12345678", Role.ADMIN, "INR");
        seedUserIfMissing("analyst@gmail.com", "Analyst", "User", "12345678", Role.ANALYST, "INR");
    }

    private void seedUserIfMissing(
            String email,
            String firstName,
            String lastName,
            String password,
            Role role,
            String currency
    ) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
            .active(true)
                .currency(currency)
                .build();

        userRepository.save(user);
        System.out.println("Seeded " + role + " user: " + email);
    }
}
