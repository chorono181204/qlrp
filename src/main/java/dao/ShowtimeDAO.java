package dao;

import model.ShowtimeSummary;
import utils.JdbcUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeDAO {

    public List<ShowtimeSummary> findAll(String whereClause, String orderBy, String keyword) {
        String sql = """
                SELECT s.id, m.title AS movie_title, c.name AS cinema_name, r.name AS room_name,
                       s.show_date, s.start_time, s.end_time, s.base_price, s.status
                FROM showtimes s
                JOIN movies m ON m.id = s.movie_id
                JOIN rooms r ON r.id = s.room_id
                JOIN cinemas c ON c.id = r.cinema_id
                """ + whereClause + """
                ORDER BY """ + orderBy;

        List<ShowtimeSummary> showtimes = new ArrayList<>();

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
                    showtimes.add(new ShowtimeSummary(
                            resultSet.getInt("id"),
                            resultSet.getString("movie_title"),
                            resultSet.getString("cinema_name"),
                            resultSet.getString("room_name"),
                            resultSet.getDate("show_date").toLocalDate(),
                            resultSet.getTime("start_time").toLocalTime(),
                            resultSet.getTime("end_time").toLocalTime(),
                            resultSet.getBigDecimal("base_price"),
                            resultSet.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach lich chieu: " + e.getMessage());
        }

        return showtimes;
    }

    public boolean hasConflict(int roomId, LocalDate showDate, LocalTime startTime, LocalTime endTime) {
        String sql = """
                SELECT 1
                FROM showtimes
                WHERE room_id = ?
                  AND show_date = ?
                  AND start_time < ?
                  AND end_time > ?
                LIMIT 1
                """;

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, roomId);
            preparedStatement.setDate(2, Date.valueOf(showDate));
            preparedStatement.setTime(3, Time.valueOf(endTime));
            preparedStatement.setTime(4, Time.valueOf(startTime));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println("Loi khi kiem tra trung lich chieu: " + e.getMessage());
            return true;
        }
    }

    public boolean create(int movieId, int roomId, LocalDate showDate, LocalTime startTime,
                          LocalTime endTime, BigDecimal basePrice, String status) {
        String sql = """
                INSERT INTO showtimes (movie_id, room_id, show_date, start_time, end_time, base_price, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setDate(3, Date.valueOf(showDate));
            preparedStatement.setTime(4, Time.valueOf(startTime));
            preparedStatement.setTime(5, Time.valueOf(endTime));
            preparedStatement.setBigDecimal(6, basePrice);
            preparedStatement.setString(7, status);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi them lich chieu: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM showtimes WHERE id = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi xoa lich chieu: " + e.getMessage());
            return false;
        }
    }

    public boolean hasConflictExcludingId(int showtimeId, int roomId, LocalDate showDate, LocalTime startTime, LocalTime endTime) {
        String sql = """
                SELECT 1
                FROM showtimes
                WHERE room_id = ?
                  AND show_date = ?
                  AND start_time < ?
                  AND end_time > ?
                  AND id <> ?
                LIMIT 1
                """;

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, roomId);
            preparedStatement.setDate(2, Date.valueOf(showDate));
            preparedStatement.setTime(3, Time.valueOf(endTime));
            preparedStatement.setTime(4, Time.valueOf(startTime));
            preparedStatement.setInt(5, showtimeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println("Loi khi kiem tra trung lich chieu khi cap nhat: " + e.getMessage());
            return true;
        }
    }

    public boolean update(int showtimeId, int movieId, int roomId, LocalDate showDate, LocalTime startTime,
                          LocalTime endTime, BigDecimal basePrice, String status) {
        String sql = """
                UPDATE showtimes
                SET movie_id = ?, room_id = ?, show_date = ?, start_time = ?, end_time = ?, base_price = ?, status = ?
                WHERE id = ?
                """;

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setDate(3, Date.valueOf(showDate));
            preparedStatement.setTime(4, Time.valueOf(startTime));
            preparedStatement.setTime(5, Time.valueOf(endTime));
            preparedStatement.setBigDecimal(6, basePrice);
            preparedStatement.setString(7, status);
            preparedStatement.setInt(8, showtimeId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi cap nhat lich chieu: " + e.getMessage());
            return false;
        }
    }
}
