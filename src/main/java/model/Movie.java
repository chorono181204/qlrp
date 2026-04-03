package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Movie(
        int id,
        String title,
        int durationMinutes,
        String director,
        String actors,
        String language,
        String ageRating,
        LocalDate releaseDate,
        LocalDate endDate,
        String description,
        String posterUrl,
        String status,
        LocalDateTime createdAt
) {
}
