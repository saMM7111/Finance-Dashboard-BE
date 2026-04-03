package com.sankalp.financedashboard.config;

import com.sankalp.financedashboard.repository.UserRepository;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    //contact for open api documentation
    private Contact contact() {
        Contact contact = new Contact();
        contact.email("sammehta1000@gmail.com");
        contact.name("Sankalp Mehta");
        return contact;
    }

    //security scheme for open api documentation
    @Bean
    public SecurityScheme securityScheme() {
        return new SecurityScheme()
                .bearerFormat("barer format")
                .in(SecurityScheme.In.HEADER)
                .description("description");
    }

    //automatic documentation by open api
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                                .title("Finance Dashboard")
                                .version("1.0")
                                .contact(contact())
                                .description("""
                                        ## RBAC Matrix
                                        
                                        | Area | ADMIN | ANALYST | USER |
                                        | --- | --- | --- | --- |
                                        | Users | Full management | No access | Self only for read, update, delete |
                                        | Accounts | Full management | No access | Own accounts only |
                                        | Records | Full read and write | Read all records only | Own records only |
                                        | Categories | Full management + analytic | Read categories + analytic | Read categories + own analytic |
                                        | Analytics and Summaries | Access any user | Access any user | Own data only |
                                        
                                        Notes:
                                        - ANALYST is read-only and cannot create, update, or delete users, accounts, records, or categories.
                                        - Authorities are stored as raw role names: ADMIN, ANALYST, USER.
                                        """)
                             // .termsOfService("http://swagger.io/terms/")
                             // .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                )
                .components(new Components()
                        .addSecuritySchemes("bearer-key", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                );
    }
}
