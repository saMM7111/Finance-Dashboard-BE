package com.sankalp.financedashboard.config;

import com.sankalp.financedashboard.authentication.JwtAuthenticationFilter;
import com.sankalp.financedashboard.entity.Role;
import com.sankalp.financedashboard.error.handler.CustomAccessDeniedHandler;
import com.sankalp.financedashboard.error.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true) //enables @Secured annotation
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationProvider authenticationProvider;

    //paths, which don't require authentication
    private final String[] noAuthPaths = {
            // endpoint for check that the app is running
            "/",
            // authentication and registration endpoints
            "/auth/**",
            "/api/auth/**",
            // documentation endpoints
            "/swagger-resources/**",
            "/api/swagger-resources/**",
            "/swagger-ui/**",
            "/api/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v3/api-docs/**"
    };

    //paths, which require role ADMIN
    private final String[] adminPaths = {};

    //enables cors for front end
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "content-type"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "content-type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                    .cors()
                .and()
                    .csrf()
                    .disable()
                    .authorizeHttpRequests()
                    .requestMatchers(noAuthPaths)
                    .permitAll()
                    .requestMatchers(adminPaths)
                    .hasAuthority(Role.ADMIN.name())
                    .anyRequest()
                    .authenticated()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //not saves session
                .and()
                    .authenticationProvider(authenticationProvider) //set custom user details service
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling()
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                    .accessDeniedHandler(new CustomAccessDeniedHandler());

        return httpSecurity.build();
    }
}
