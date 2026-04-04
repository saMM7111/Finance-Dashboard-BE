package com.sankalp.financedashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class FinanceDashboard {

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public String displayAppIsRunning() {
        return "Server is running. Welcome to the Finance Dashboard API! " +
                "Use the /api/auth endpoints to register and authenticate. " +
                "Access the API documentation at /swagger-ui/index.html.";
    }

    public static void main(String[] args) {
        SpringApplication.run(FinanceDashboard.class, args);
    }
}
