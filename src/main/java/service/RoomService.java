package service;

import dao.RoomDAO;
import model.RoomSummary;

import java.util.List;
import java.util.Set;

public class RoomService {
    private static final Set<String> ALLOWED_ROOM_TYPES = Set.of("2D", "3D", "IMAX", "4DX");
    private static final Set<String> ALLOWED_SORTS = Set.of("id", "cinema_name", "room_name", "capacity", "room_type");
    private final RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAO();
    }

    public List<RoomSummary> findAll(String sortBy) {
        return roomDAO.findAll("", normalizeSort(sortBy), null);
    }

    public List<RoomSummary> search(String keyword, String sortBy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(sortBy);
        }
        return roomDAO.findAll(
                "WHERE LOWER(c.name) LIKE LOWER(?) OR LOWER(r.name) LIKE LOWER(?) OR LOWER(r.room_type) LIKE LOWER(?)",
                normalizeSort(sortBy),
                keyword.trim()
        );
    }

    public String create(String cinemaIdText, String roomName, String capacityText, String roomType) {
        int cinemaId;
        int capacity;

        try {
            cinemaId = Integer.parseInt(cinemaIdText.trim());
            capacity = Integer.parseInt(capacityText.trim());
        } catch (NumberFormatException e) {
            return "ID rap va suc chua phai la so nguyen.";
        }

        if (cinemaId <= 0) {
            return "ID rap khong hop le.";
        }

        if (roomName == null || roomName.trim().isEmpty()) {
            return "Ten phong khong duoc de trong.";
        }

        if (capacity <= 0) {
            return "Suc chua phong phai lon hon 0.";
        }

        String normalizedRoomType = roomType == null || roomType.isBlank() ? "2D" : roomType.trim().toUpperCase();
        if (!ALLOWED_ROOM_TYPES.contains(normalizedRoomType)) {
            return "Loai phong chi nhan 2D, 3D, IMAX hoac 4DX.";
        }

        return roomDAO.create(cinemaId, roomName.trim(), capacity, normalizedRoomType)
                ? "Them phong thanh cong."
                : "Them phong that bai.";
    }

    public String update(String roomIdText, String cinemaIdText, String roomName, String capacityText, String roomType) {
        int roomId;
        int cinemaId;
        int capacity;

        try {
            roomId = Integer.parseInt(roomIdText.trim());
            cinemaId = Integer.parseInt(cinemaIdText.trim());
            capacity = Integer.parseInt(capacityText.trim());
        } catch (NumberFormatException e) {
            return "ID phong, ID rap va suc chua phai la so nguyen.";
        }

        if (roomId <= 0 || cinemaId <= 0) {
            return "ID phong va ID rap khong hop le.";
        }

        if (roomName == null || roomName.trim().isEmpty()) {
            return "Ten phong khong duoc de trong.";
        }

        if (capacity <= 0) {
            return "Suc chua phong phai lon hon 0.";
        }

        String normalizedRoomType = roomType == null || roomType.isBlank() ? "2D" : roomType.trim().toUpperCase();
        if (!ALLOWED_ROOM_TYPES.contains(normalizedRoomType)) {
            return "Loai phong chi nhan 2D, 3D, IMAX hoac 4DX.";
        }

        return roomDAO.update(roomId, cinemaId, roomName.trim(), capacity, normalizedRoomType)
                ? "Cap nhat phong thanh cong."
                : "Cap nhat phong that bai.";
    }

    public String delete(String roomIdText) {
        int roomId;

        try {
            roomId = Integer.parseInt(roomIdText.trim());
        } catch (NumberFormatException e) {
            return "ID phong phai la so nguyen.";
        }

        if (roomId <= 0) {
            return "ID phong khong hop le.";
        }

        return roomDAO.deleteById(roomId) ? "Xoa phong thanh cong." : "Xoa phong that bai.";
    }

    private String normalizeSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "r.id";
        }
        String normalized = sortBy.trim().toLowerCase();
        if (!ALLOWED_SORTS.contains(normalized)) {
            return "r.id";
        }
        return switch (normalized) {
            case "cinema_name" -> "c.name";
            case "room_name" -> "r.name";
            case "capacity" -> "r.capacity";
            case "room_type" -> "r.room_type";
            default -> "r.id";
        };
    }
}
