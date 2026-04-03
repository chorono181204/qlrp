package model;

public record Room(
        int id,
        int cinemaId,
        String name,
        int capacity,
        String roomType,
        String status
) {
}
