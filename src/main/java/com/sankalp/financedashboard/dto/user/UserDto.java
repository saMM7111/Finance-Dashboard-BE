package com.sankalp.financedashboard.dto.user;

import com.sankalp.financedashboard.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @Size(max = 40)
    private String firstName;

    @Size(max = 40)
    private String lastName;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean active;

    @Size(min = 1, max = 40)
    private String currency;

    private List<Long> accountIds;

    /**
     * Backward-compatible constructor used by existing tests and call sites.
     */
    public UserDto(
            Long id,
            String firstName,
            String lastName,
            String email,
            Role role,
            String currency,
            List<Long> accountIds
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.active = true;
        this.currency = currency;
        this.accountIds = accountIds;
    }
}
