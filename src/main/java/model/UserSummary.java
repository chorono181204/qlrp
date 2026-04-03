package model;

import java.time.LocalDateTime;

public record UserSummary(
        int id,
        String fullName,
        String username,
        String phone,
        String email,
        String roleName,
        LocalDateTime createdAt
) {
}
