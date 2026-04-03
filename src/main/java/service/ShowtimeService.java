package service;

import dao.ShowtimeDAO;
import model.ShowtimeSummary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

public class ShowtimeService {
    private static final Set<String> ALLOWED_STATUSES = Set.of("OPEN", "CLOSED", "CANCELLED");
    private static final Set<String> ALLOWED_SORTS = Set.of("id", "show_date", "start_time", "movie_title", "cinema_name", "room_name", "status");
    private final ShowtimeDAO showtimeDAO;

    public ShowtimeService() {
        this.showtimeDAO = new ShowtimeDAO();
    }

    public List<ShowtimeSummary> findAll(String sortBy) {
        return showtimeDAO.findAll("", normalizeSort(sortBy), null);
    }

    public List<ShowtimeSummary> search(String keyword, String sortBy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(sortBy);
        }
        return showtimeDAO.findAll(
                "WHERE LOWER(m.title) LIKE LOWER(?) OR LOWER(c.name) LIKE LOWER(?) OR LOWER(r.name) LIKE LOWER(?)",
                normalizeSort(sortBy),
                keyword.trim()
        );
    }

    public String create(String movieIdText, String roomIdText, String showDateText, String startTimeText,
                         String endTimeText, String basePriceText, String statusText) {
        int movieId;
        int roomId;
        LocalDate showDate;
        LocalTime startTime;
        LocalTime endTime;
        BigDecimal basePrice;

        try {
            movieId = Integer.parseInt(movieIdText.trim());
            roomId = Integer.parseInt(roomIdText.trim());
            showDate = LocalDate.parse(showDateText.trim());
            startTime = LocalTime.parse(startTimeText.trim());
            endTime = LocalTime.parse(endTimeText.trim());
            basePrice = new BigDecimal(basePriceText.trim());
        } catch (NumberFormatException e) {
            return "ID phim, ID phong va gia ve phai dung dinh dang so.";
        } catch (DateTimeParseException e) {
            return "Ngay chieu phai theo dang YYYY-MM-DD va gio theo dang HH:MM.";
        }

        if (movieId <= 0 || roomId <= 0) {
            return "ID phim va ID phong phai lon hon 0.";
        }

        if (!endTime.isAfter(startTime)) {
            return "Gio ket thuc phai lon hon gio bat dau.";
        }

        if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return "Gia ve co ban phai lon hon 0.";
        }

        String status = statusText == null || statusText.isBlank() ? "OPEN" : statusText.trim().toUpperCase();
        if (!ALLOWED_STATUSES.contains(status)) {
            return "Trang thai lich chieu chi nhan OPEN, CLOSED hoac CANCELLED.";
        }

        if (showtimeDAO.hasConflict(roomId, showDate, startTime, endTime)) {
            return "Phong da co lich chieu trung thoi gian.";
        }

        return showtimeDAO.create(movieId, roomId, showDate, startTime, endTime, basePrice, status)
                ? "Them lich chieu thanh cong."
                : "Them lich chieu that bai.";
    }

    public String delete(String showtimeIdText) {
        int showtimeId;

        try {
            showtimeId = Integer.parseInt(showtimeIdText.trim());
        } catch (NumberFormatException e) {
            return "ID lich chieu phai la so nguyen.";
        }

        if (showtimeId <= 0) {
            return "ID lich chieu khong hop le.";
        }

        return showtimeDAO.deleteById(showtimeId) ? "Xoa lich chieu thanh cong." : "Khong tim thay lich chieu de xoa.";
    }

    public String update(String showtimeIdText, String movieIdText, String roomIdText, String showDateText,
                         String startTimeText, String endTimeText, String basePriceText, String statusText) {
        int showtimeId;
        int movieId;
        int roomId;
        LocalDate showDate;
        LocalTime startTime;
        LocalTime endTime;
        BigDecimal basePrice;

        try {
            showtimeId = Integer.parseInt(showtimeIdText.trim());
            movieId = Integer.parseInt(movieIdText.trim());
            roomId = Integer.parseInt(roomIdText.trim());
            showDate = LocalDate.parse(showDateText.trim());
            startTime = LocalTime.parse(startTimeText.trim());
            endTime = LocalTime.parse(endTimeText.trim());
            basePrice = new BigDecimal(basePriceText.trim());
        } catch (NumberFormatException e) {
            return "ID lich chieu, ID phim, ID phong va gia ve phai dung dinh dang so.";
        } catch (DateTimeParseException e) {
            return "Ngay chieu phai theo dang YYYY-MM-DD va gio theo dang HH:MM.";
        }

        if (showtimeId <= 0 || movieId <= 0 || roomId <= 0) {
            return "ID lich chieu, ID phim va ID phong phai lon hon 0.";
        }

        if (!endTime.isAfter(startTime)) {
            return "Gio ket thuc phai lon hon gio bat dau.";
        }

        if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return "Gia ve co ban phai lon hon 0.";
        }

        String status = statusText == null || statusText.isBlank() ? "OPEN" : statusText.trim().toUpperCase();
        if (!ALLOWED_STATUSES.contains(status)) {
            return "Trang thai lich chieu chi nhan OPEN, CLOSED hoac CANCELLED.";
        }

        if (showtimeDAO.hasConflictExcludingId(showtimeId, roomId, showDate, startTime, endTime)) {
            return "Phong da co lich chieu trung thoi gian.";
        }

        return showtimeDAO.update(showtimeId, movieId, roomId, showDate, startTime, endTime, basePrice, status)
                ? "Cap nhat lich chieu thanh cong."
                : "Cap nhat lich chieu that bai.";
    }

    private String normalizeSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "s.id";
        }
        String normalized = sortBy.trim().toLowerCase();
        if (!ALLOWED_SORTS.contains(normalized)) {
            return "s.id";
        }
        return switch (normalized) {
            case "show_date" -> "s.show_date";
            case "start_time" -> "s.start_time";
            case "movie_title" -> "m.title";
            case "cinema_name" -> "c.name";
            case "room_name" -> "r.name";
            case "status" -> "s.status";
            default -> "s.id";
        };
    }
}
