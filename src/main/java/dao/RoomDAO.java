package dao;

import model.RoomSummary;
import utils.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<RoomSummary> findAll(String whereClause, String orderBy, String keyword) {
        String sql = """
                SELECT r.id, c.name AS cinema_name, r.name AS room_name, r.capacity, r.room_type, r.status
                FROM rooms r
                JOIN cinemas c ON c.id = r.cinema_id
                """ + whereClause + """
                ORDER BY """ + orderBy;
        List<RoomSummary> rooms = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (keyword != null) {
                String pattern = "%" + keyword + "%";
                preparedStatement.setString(1, pattern);
                preparedStatement.setString(2, pattern);
                preparedStatement.setString(3, pattern);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rooms.add(new RoomSummary(
                            resultSet.getInt("id"),
                            resultSet.getString("cinema_name"),
                            resultSet.getString("room_name"),
                            resultSet.getInt("capacity"),
                            resultSet.getString("room_type"),
                            resultSet.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach phong: " + e.getMessage());
        }

        return rooms;
    }

    public boolean create(int cinemaId, String roomName, int capacity, String roomType) {
        String sql = "INSERT INTO rooms (cinema_id, name, capacity, room_type, status) VALUES (?, ?, ?, ?, 'ACTIVE')";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, cinemaId);
            preparedStatement.setString(2, roomName);
            preparedStatement.setInt(3, capacity);
            preparedStatement.setString(4, roomType);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi them phong: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int roomId, int cinemaId, String roomName, int capacity, String roomType) {
        String sql = """
                UPDATE rooms
                SET cinema_id = ?, name = ?, capacity = ?, room_type = ?
                WHERE id = ?
                """;

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, cinemaId);
            preparedStatement.setString(2, roomName);
            preparedStatement.setInt(3, capacity);
            preparedStatement.setString(4, roomType);
            preparedStatement.setInt(5, roomId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi cap nhat phong: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteById(int roomId) {
        String sql = "DELETE FROM rooms WHERE id = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, roomId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi xoa phong: " + e.getMessage());
            return false;
        }
    }
}
