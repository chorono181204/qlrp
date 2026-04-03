package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BookingSummary(
        int bookingId,
        String bookingCode,
        String username,
        String movieTitle,
        String cinemaName,
        String roomName,
        LocalDate showDate,
        LocalTime startTime,
        String seats,
        BigDecimal totalAmount,
        String status,
        LocalDateTime bookingTime
) {
}
