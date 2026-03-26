package com.example.tms.api.dto.auth;

import com.example.tms.entity.enums.RoleName;
import jakarta.validation.constraints.NotNull;

public record SwitchRoleRequest(
        @NotNull RoleName activeRole
) {
}
