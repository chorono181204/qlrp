package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record Showtime(
        int id,
        int movieId,
        int roomId,
        LocalDate showDate,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal basePrice,
        String status
) {
}
