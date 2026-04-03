package service;

import dao.BookingDAO;
import model.BookingSummary;
import model.SeatAvailability;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BookingService {
    private static final Set<String> ALLOWED_PAYMENT_METHODS = Set.of("CASH", "CARD", "MOMO");
    private static final Set<String> ALLOWED_SORTS = Set.of("booking_time", "booking_code", "movie_title", "cinema_name", "total_amount", "status");
    private final BookingDAO bookingDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
    }

    public List<SeatAvailability> findSeatsByShowtime(String showtimeIdText) {
        try {
            int showtimeId = Integer.parseInt(showtimeIdText.trim());
            return bookingDAO.findSeatsByShowtime(showtimeId);
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    public String createBooking(int userId, String showtimeIdText, String seatCodesText, String paymentMethodText) {
        int showtimeId;

        try {
            showtimeId = Integer.parseInt(showtimeIdText.trim());
        } catch (NumberFormatException e) {
            return "ID lich chieu phai la so nguyen.";
        }

        if (showtimeId <= 0) {
            return "ID lich chieu khong hop le.";
        }

        if (!bookingDAO.showtimeExists(showtimeId)) {
            return "Khong tim thay lich chieu.";
        }

        List<String> seatCodes = parseSeatCodes(seatCodesText);
        if (seatCodes.isEmpty()) {
            return "Ban phai chon it nhat 1 ghe.";
        }

        String paymentMethod = paymentMethodText == null || paymentMethodText.isBlank()
                ? "CASH"
                : paymentMethodText.trim().toUpperCase();
        if (!ALLOWED_PAYMENT_METHODS.contains(paymentMethod)) {
            return "Phuong thuc thanh toan chi nhan CASH, CARD hoac MOMO.";
        }

        boolean isSuccess = bookingDAO.createBooking(userId, showtimeId, seatCodes, paymentMethod);
        return isSuccess
                ? "Dat ve thanh cong."
                : "Dat ve that bai. Co the ghe da bi dat hoac du lieu chua hop le.";
    }

    public List<BookingSummary> findBookingsByUserId(int userId) {
        return bookingDAO.findBookingsByUserId(userId);
    }

    public List<BookingSummary> findAllBookings() {
        return bookingDAO.findAllBookings();
    }

    public List<BookingSummary> searchBookings(String keyword, String sortBy, Integer userId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return sortBookings(sortBy, userId);
        }
        return bookingDAO.searchBookings(keyword.trim(), normalizeSort(sortBy), userId);
    }

    public List<BookingSummary> sortBookings(String sortBy, Integer userId) {
        String orderBy = normalizeSort(sortBy);
        if (userId == null) {
            return bookingDAO.findAllBookings(orderBy);
        }
        if ("b.booking_time DESC".equals(orderBy)) {
            return bookingDAO.findBookingsByUserId(userId);
        }
        return bookingDAO.searchBookings("", orderBy, userId);
    }

    public String cancelBookingByUser(int userId, String bookingIdText) {
        int bookingId;

        try {
            bookingId = Integer.parseInt(bookingIdText.trim());
        } catch (NumberFormatException e) {
            return "ID booking phai la so nguyen.";
        }

        if (bookingId <= 0) {
            return "ID booking khong hop le.";
        }

        return bookingDAO.cancelBookingByUser(bookingId, userId)
                ? "Huy ve thanh cong."
                : "Khong the huy booking nay.";
    }

    public String cancelBooking(String bookingIdText) {
        int bookingId;

        try {
            bookingId = Integer.parseInt(bookingIdText.trim());
        } catch (NumberFormatException e) {
            return "ID booking phai la so nguyen.";
        }

        if (bookingId <= 0) {
            return "ID booking khong hop le.";
        }

        return bookingDAO.cancelBooking(bookingId)
                ? "Huy ve thanh cong."
                : "Khong the huy booking nay.";
    }

    private List<String> parseSeatCodes(String seatCodesText) {
        if (seatCodesText == null || seatCodesText.trim().isEmpty()) {
            return List.of();
        }

        String[] parts = seatCodesText.split(",");
        Set<String> uniqueSeatCodes = new LinkedHashSet<>();
        for (String part : parts) {
            String seatCode = part.trim().toUpperCase();
            if (!seatCode.isEmpty()) {
                uniqueSeatCodes.add(seatCode);
            }
        }

        return new ArrayList<>(uniqueSeatCodes);
    }

    private String normalizeSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "b.booking_time DESC";
        }

        String normalized = sortBy.trim().toLowerCase();
        if (!ALLOWED_SORTS.contains(normalized)) {
            return "b.booking_time DESC";
        }

        return switch (normalized) {
            case "booking_code" -> "b.booking_code";
            case "movie_title" -> "m.title";
            case "cinema_name" -> "c.name";
            case "total_amount" -> "b.total_amount DESC";
            case "status" -> "b.status";
            default -> "b.booking_time DESC";
        };
    }
}
