package model;

import java.math.BigDecimal;

public record SeatAvailability(
        int seatId,
        String seatCode,
        String seatTypeName,
        BigDecimal priceMultiplier,
        boolean booked
) {
}
