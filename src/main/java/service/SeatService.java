package service;

import dao.SeatDAO;
import model.SeatSummary;
import model.SeatType;

import java.util.List;
import java.util.Set;

public class SeatService {
    private static final Set<String> ALLOWED_SORTS = Set.of("seat_code", "seat_type_name", "status");
    private final SeatDAO seatDAO;

    public SeatService() {
        this.seatDAO = new SeatDAO();
    }

    public List<SeatType> findSeatTypes() {
        return seatDAO.findSeatTypes();
    }

    public List<SeatSummary> findByRoomId(String roomIdText) {
        try {
            int roomId = Integer.parseInt(roomIdText.trim());
            return seatDAO.findByRoomId(roomId);
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    public List<SeatSummary> searchByRoomId(String roomIdText, String keyword, String sortBy) {
        try {
            int roomId = Integer.parseInt(roomIdText.trim());
            if (keyword == null || keyword.trim().isEmpty()) {
                return seatDAO.findByRoomId(roomId, "", normalizeSort(sortBy), null);
            }
            return seatDAO.findByRoomId(
                    roomId,
                    "AND (LOWER(CONCAT(s.seat_row, s.seat_number)) LIKE LOWER(?) OR LOWER(st.name) LIKE LOWER(?))",
                    normalizeSort(sortBy),
                    keyword.trim()
            );
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    public List<SeatSummary> sortByRoomId(String roomIdText, String sortBy) {
        try {
            int roomId = Integer.parseInt(roomIdText.trim());
            return seatDAO.findByRoomId(roomId, "", normalizeSort(sortBy), null);
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    public String generateSeats(String roomIdText, String rowCountText, String seatsPerRowText, String seatTypeIdText) {
        int roomId;
        int rowCount;
        int seatsPerRow;
        int seatTypeId;

        try {
            roomId = Integer.parseInt(roomIdText.trim());
            rowCount = Integer.parseInt(rowCountText.trim());
            seatsPerRow = Integer.parseInt(seatsPerRowText.trim());
            seatTypeId = Integer.parseInt(seatTypeIdText.trim());
        } catch (NumberFormatException e) {
            return "Thong tin phong, so hang, so ghe va loai ghe phai la so nguyen.";
        }

        if (roomId <= 0 || rowCount <= 0 || seatsPerRow <= 0 || seatTypeId <= 0) {
            return "Cac gia tri nhap vao phai lon hon 0.";
        }

        if (rowCount > 26) {
            return "Toi da 26 hang ghe tu A den Z.";
        }

        return seatDAO.generateSeats(roomId, rowCount, seatsPerRow, seatTypeId)
                ? "Tao danh sach ghe thanh cong."
                : "Tao danh sach ghe that bai.";
    }

    private String normalizeSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "s.seat_row, s.seat_number";
        }
        String normalized = sortBy.trim().toLowerCase();
        if (!ALLOWED_SORTS.contains(normalized)) {
            return "s.seat_row, s.seat_number";
        }
        return switch (normalized) {
            case "seat_type_name" -> "st.name";
            case "status" -> "s.status";
            default -> "s.seat_row, s.seat_number";
        };
    }
}
