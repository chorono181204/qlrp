package model;

import java.math.BigDecimal;

public record BookingDetail(
        int id,
        int bookingId,
        int showtimeId,
        int seatId,
        BigDecimal ticketPrice,
        String status
) {
}
