package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Payment(
        int id,
        int bookingId,
        String paymentMethod,
        BigDecimal amount,
        LocalDateTime paymentTime,
        String status
) {
}
