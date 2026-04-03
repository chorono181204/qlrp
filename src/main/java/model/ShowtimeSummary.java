package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeSummary(
        int id,
        String movieTitle,
        String cinemaName,
        String roomName,
        LocalDate showDate,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal basePrice,
        String status
) {
}
