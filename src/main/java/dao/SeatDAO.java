package dao;

import model.SeatSummary;
import model.SeatType;
import utils.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    public List<SeatType> findSeatTypes() {
        String sql = "SELECT id, name, price_multiplier FROM seat_types ORDER BY id";
        List<SeatType> seatTypes = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                seatTypes.add(new SeatType(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getBigDecimal("price_multiplier")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay loai ghe: " + e.getMessage());
        }

        return seatTypes;
    }

    public List<SeatSummary> findByRoomId(int roomId) {
        return findByRoomId(roomId, "", "s.seat_row, s.seat_number", null);
    }

    public List<SeatSummary> findByRoomId(int roomId, String whereClause, String orderBy, String keyword) {
        String sql = """
                SELECT s.id, r.name AS room_name, CONCAT(s.seat_row, s.seat_number) AS seat_code,
                       st.name AS seat_type_name, s.status
                FROM seats s
                JOIN rooms r ON r.id = s.room_id
                JOIN seat_types st ON st.id = s.seat_type_id
                WHERE s.room_id = ?
                """ + whereClause + """
                ORDER BY """ + orderBy;
        List<SeatSummary> seats = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, roomId);
            if (keyword != null) {
                String pattern = "%" + keyword + "%";
                preparedStatement.setString(2, pattern);
                preparedStatement.setString(3, pattern);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    seats.add(new SeatSummary(
                            resultSet.getInt("id"),
                            resultSet.getString("room_name"),
                            resultSet.getString("seat_code"),
                            resultSet.getString("seat_type_name"),
                            resultSet.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach ghe: " + e.getMessage());
        }

        return seats;
    }

    public boolean generateSeats(int roomId, int rowCount, int seatsPerRow, int seatTypeId) {
        String deleteSql = "DELETE FROM seats WHERE room_id = ?";
        String insertSql = "INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status) VALUES (?, ?, ?, ?, 'ACTIVE')";

        try (Connection connection = JdbcUtil.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                 PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                deleteStatement.setInt(1, roomId);
                deleteStatement.executeUpdate();

                for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                    String seatRow = String.valueOf((char) ('A' + rowIndex));
                    for (int seatNumber = 1; seatNumber <= seatsPerRow; seatNumber++) {
                        insertStatement.setInt(1, roomId);
                        insertStatement.setString(2, seatRow);
                        insertStatement.setInt(3, seatNumber);
                        insertStatement.setInt(4, seatTypeId);
                        insertStatement.addBatch();
                    }
                }

                insertStatement.executeBatch();
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Loi khi tao danh sach ghe: " + e.getMessage());
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Loi khi ket noi de tao ghe: " + e.getMessage());
            return false;
        }
    }
}
