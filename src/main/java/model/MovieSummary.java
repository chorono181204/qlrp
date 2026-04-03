package model;

public record MovieSummary(
        int id,
        String title,
        int durationMinutes,
        String director,
        String status,
        String genres
) {
}
