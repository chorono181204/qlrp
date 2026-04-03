package model;

public record SeatSummary(
        int id,
        String roomName,
        String seatCode,
        String seatTypeName,
        String status
) {
}
