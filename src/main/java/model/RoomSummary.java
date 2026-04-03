package model;

public record RoomSummary(
        int id,
        String cinemaName,
        String roomName,
        int capacity,
        String roomType,
        String status
) {
}
