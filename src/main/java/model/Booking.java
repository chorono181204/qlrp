package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Booking(
        int id,
        int userId,
        String bookingCode,
        LocalDateTime bookingTime,
        BigDecimal totalAmount,
        String status
) {
}
