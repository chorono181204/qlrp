package service;

import dao.MovieDAO;
import model.MovieSummary;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MovieService {
    private static final Set<String> ALLOWED_STATUSES = Set.of("COMING_SOON", "NOW_SHOWING", "STOPPED");
    private static final Set<String> ALLOWED_SORTS = Set.of("id", "title", "duration_minutes", "status");
    private final MovieDAO movieDAO;

    public MovieService() {
        this.movieDAO = new MovieDAO();
    }

    public List<MovieSummary> findAll(String sortBy) {
        return movieDAO.findAll("", normalizeSort(sortBy), null);
    }

    public List<MovieSummary> search(String keyword, String sortBy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(sortBy);
        }
        return movieDAO.findAll(
                "WHERE LOWER(m.title) LIKE LOWER(?) OR LOWER(COALESCE(m.director, '')) LIKE LOWER(?) OR LOWER(COALESCE(m.status, '')) LIKE LOWER(?)",
                normalizeSort(sortBy),
                keyword.trim()
        );
    }

    public String create(String title, String durationText, String director, String language,
                         String ageRating, String description, String status, String genreIdsText) {
        if (title == null || title.trim().isEmpty()) {
            return "Ten phim khong duoc de trong.";
        }

        int durationMinutes;
        try {
            durationMinutes = Integer.parseInt(durationText.trim());
        } catch (NumberFormatException e) {
            return "Thoi luong phim phai la so nguyen.";
        }

        if (durationMinutes <= 0) {
            return "Thoi luong phim phai lon hon 0.";
        }

        String normalizedStatus = normalizeStatus(status);
        if (!ALLOWED_STATUSES.contains(normalizedStatus)) {
            return "Trang thai phim chi nhan COMING_SOON, NOW_SHOWING hoac STOPPED.";
        }

        List<Integer> genreIds;
        try {
            genreIds = parseGenreIds(genreIdsText);
        } catch (NumberFormatException e) {
            return "Danh sach the loai phai la cac ID cach nhau boi dau phay.";
        }

        boolean isSuccess = movieDAO.create(
                title.trim(),
                durationMinutes,
                director,
                language,
                ageRating,
                description,
                normalizedStatus,
                genreIds
        );

        return isSuccess ? "Them phim thanh cong." : "Them phim that bai.";
    }

    public String delete(String movieIdText) {
        int movieId;
        try {
            movieId = Integer.parseInt(movieIdText.trim());
        } catch (NumberFormatException e) {
            return "ID phim phai la so nguyen.";
        }

        if (movieId <= 0) {
            return "ID phim khong hop le.";
        }

        return movieDAO.deleteById(movieId) ? "Xoa phim thanh cong." : "Khong tim thay phim de xoa.";
    }

    public String update(String movieIdText, String title, String durationText, String director, String language,
                         String ageRating, String description, String status, String genreIdsText) {
        int movieId;
        int durationMinutes;

        try {
            movieId = Integer.parseInt(movieIdText.trim());
            durationMinutes = Integer.parseInt(durationText.trim());
        } catch (NumberFormatException e) {
            return "ID phim va thoi luong phai la so nguyen.";
        }

        if (movieId <= 0) {
            return "ID phim khong hop le.";
        }

        if (title == null || title.trim().isEmpty()) {
            return "Ten phim khong duoc de trong.";
        }

        if (durationMinutes <= 0) {
            return "Thoi luong phim phai lon hon 0.";
        }

        String normalizedStatus = normalizeStatus(status);
        if (!ALLOWED_STATUSES.contains(normalizedStatus)) {
            return "Trang thai phim chi nhan COMING_SOON, NOW_SHOWING hoac STOPPED.";
        }

        List<Integer> genreIds;
        try {
            genreIds = parseGenreIds(genreIdsText);
        } catch (NumberFormatException e) {
            return "Danh sach the loai phai la cac ID cach nhau boi dau phay.";
        }

        boolean isSuccess = movieDAO.update(
                movieId,
                title.trim(),
                durationMinutes,
                director,
                language,
                ageRating,
                description,
                normalizedStatus,
                genreIds
        );

        return isSuccess ? "Cap nhat phim thanh cong." : "Cap nhat phim that bai.";
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "COMING_SOON";
        }
        return status.trim().toUpperCase();
    }

    private List<Integer> parseGenreIds(String genreIdsText) {
        if (genreIdsText == null || genreIdsText.trim().isEmpty()) {
            return List.of();
        }

        String[] parts = genreIdsText.split(",");
        Set<Integer> uniqueIds = new LinkedHashSet<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                uniqueIds.add(Integer.parseInt(trimmed));
            }
        }
        return new ArrayList<>(uniqueIds);
    }

    private String normalizeSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "m.id";
        }
        String normalized = sortBy.trim().toLowerCase();
        if (!ALLOWED_SORTS.contains(normalized)) {
            return "m.id";
        }
        return switch (normalized) {
            case "title" -> "m.title";
            case "duration_minutes" -> "m.duration_minutes";
            case "status" -> "m.status";
            default -> "m.id";
        };
    }
}
