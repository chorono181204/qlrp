package model;

public record Seat(
        int id,
        int roomId,
        String seatRow,
        int seatNumber,
        int seatTypeId,
        String status
) {
}
