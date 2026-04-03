package dao;

import model.BookingSummary;
import model.SeatAvailability;
import utils.JdbcUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookingDAO {

    public List<BookingSummary> searchBookings(String keyword, String orderBy, Integer userId) {
        String whereClause = userId == null
                ? """
                WHERE LOWER(b.booking_code) LIKE LOWER(?)
                   OR LOWER(u.username) LIKE LOWER(?)
                   OR LOWER(m.title) LIKE LOWER(?)
                   OR LOWER(c.name) LIKE LOWER(?)
                """
                : """
                WHERE b.user_id = ?
                  AND (
                        LOWER(b.booking_code) LIKE LOWER(?)
                     OR LOWER(u.username) LIKE LOWER(?)
                     OR LOWER(m.title) LIKE LOWER(?)
                     OR LOWER(c.name) LIKE LOWER(?)
                  )
                """;
        return findBookings(whereClause, orderBy, userId, keyword);
    }

    public List<BookingSummary> findAllBookings(String orderBy) {
        return findBookings("", orderBy, null, null);
    }

    public List<SeatAvailability> findSeatsByShowtime(int showtimeId) {
        String sql = """
                SELECT s.id AS seat_id,
                       CONCAT(s.seat_row, s.seat_number) AS seat_code,
                       st.name AS seat_type_name,
                       st.price_multiplier,
                       CASE WHEN bd.id IS NOT NULL THEN 1 ELSE 0 END AS is_booked
                FROM showtimes sh
                JOIN seats s ON s.room_id = sh.room_id
                JOIN seat_types st ON st.id = s.seat_type_id
                LEFT JOIN booking_details bd
                    ON bd.showtime_id = sh.id
                   AND bd.seat_id = s.id
                   AND bd.status = 'BOOKED'
                WHERE sh.id = ?
                ORDER BY s.seat_row, s.seat_number
                """;

        List<SeatAvailability> seats = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, showtimeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    seats.add(new SeatAvailability(
                            resultSet.getInt("seat_id"),
                            resultSet.getString("seat_code"),
                            resultSet.getString("seat_type_name"),
                            resultSet.getBigDecimal("price_multiplier"),
                            resultSet.getInt("is_booked") == 1
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach ghe cua lich chieu: " + e.getMessage());
        }

        return seats;
    }

    public boolean showtimeExists(int showtimeId) {
        String sql = "SELECT 1 FROM showtimes WHERE id = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, showtimeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println("Loi khi kiem tra lich chieu: " + e.getMessage());
            return false;
        }
    }

    public boolean createBooking(int userId, int showtimeId, List<String> seatCodes, String paymentMethod) {
        String showtimeSql = "SELECT base_price FROM showtimes WHERE id = ?";
        String seatSql = """
                SELECT s.id AS seat_id, CONCAT(s.seat_row, s.seat_number) AS seat_code, st.price_multiplier
                FROM showtimes sh
                JOIN seats s ON s.room_id = sh.room_id
                JOIN seat_types st ON st.id = s.seat_type_id
                WHERE sh.id = ?
                """;
        String bookingSql = "INSERT INTO bookings (user_id, booking_code, total_amount, status) VALUES (?, ?, ?, 'PAID')";
        String bookingDetailSql = """
                INSERT INTO booking_details (booking_id, showtime_id, seat_id, ticket_price, status)
                VALUES (?, ?, ?, ?, 'BOOKED')
                """;
        String paymentSql = """
                INSERT INTO payments (booking_id, payment_method, amount, status)
                VALUES (?, ?, ?, 'SUCCESS')
                """;

        try (Connection connection = JdbcUtil.getConnection()) {
            connection.setAutoCommit(false);

            try {
                BigDecimal basePrice = fetchBasePrice(connection, showtimeSql, showtimeId);
                if (basePrice == null) {
                    connection.rollback();
                    return false;
                }

                List<SeatAvailability> seats = fetchSeatsForBooking(connection, seatSql, showtimeId);
                List<SeatAvailability> selectedSeats = matchSelectedSeats(seats, seatCodes);
                if (selectedSeats.size() != seatCodes.size()) {
                    connection.rollback();
                    return false;
                }

                for (SeatAvailability seat : selectedSeats) {
                    if (seat.booked()) {
                        connection.rollback();
                        return false;
                    }
                }

                BigDecimal totalAmount = BigDecimal.ZERO;
                for (SeatAvailability seat : selectedSeats) {
                    totalAmount = totalAmount.add(basePrice.multiply(seat.priceMultiplier()));
                }
                totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

                int bookingId;
                String bookingCode = generateBookingCode();

                try (PreparedStatement bookingStatement =
                             connection.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS)) {
                    bookingStatement.setInt(1, userId);
                    bookingStatement.setString(2, bookingCode);
                    bookingStatement.setBigDecimal(3, totalAmount);
                    bookingStatement.executeUpdate();

                    try (ResultSet generatedKeys = bookingStatement.getGeneratedKeys()) {
                        if (!generatedKeys.next()) {
                            connection.rollback();
                            return false;
                        }
                        bookingId = generatedKeys.getInt(1);
                    }
                }

                try (PreparedStatement bookingDetailStatement = connection.prepareStatement(bookingDetailSql)) {
                    for (SeatAvailability seat : selectedSeats) {
                        BigDecimal ticketPrice = basePrice.multiply(seat.priceMultiplier()).setScale(2, RoundingMode.HALF_UP);
                        bookingDetailStatement.setInt(1, bookingId);
                        bookingDetailStatement.setInt(2, showtimeId);
                        bookingDetailStatement.setInt(3, seat.seatId());
                        bookingDetailStatement.setBigDecimal(4, ticketPrice);
                        bookingDetailStatement.addBatch();
                    }
                    bookingDetailStatement.executeBatch();
                }

                try (PreparedStatement paymentStatement = connection.prepareStatement(paymentSql)) {
                    paymentStatement.setInt(1, bookingId);
                    paymentStatement.setString(2, paymentMethod);
                    paymentStatement.setBigDecimal(3, totalAmount);
                    paymentStatement.executeUpdate();
                }

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Loi khi tao booking: " + e.getMessage());
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Loi khi ket noi de tao booking: " + e.getMessage());
            return false;
        }
    }

    public List<BookingSummary> findBookingsByUserId(int userId) {
        return findBookings("WHERE b.user_id = ?", "b.booking_time DESC", userId, null);
    }

    public List<BookingSummary> findAllBookings() {
        return findAllBookings("b.booking_time DESC");
    }

    public boolean cancelBookingByUser(int bookingId, int userId) {
        return cancelBooking("WHERE id = ? AND user_id = ? AND status = 'PAID'", bookingId, userId);
    }

    public boolean cancelBooking(int bookingId) {
        return cancelBooking("WHERE id = ? AND status = 'PAID'", bookingId, null);
    }

    private List<BookingSummary> findBookings(String whereClause, String orderBy, Integer userId, String keyword) {
        String sql = """
                SELECT b.id AS booking_id,
                       b.booking_code,
                       u.username,
                       m.title AS movie_title,
                       c.name AS cinema_name,
                       r.name AS room_name,
                       sh.show_date,
                       sh.start_time,
                       GROUP_CONCAT(CONCAT(s.seat_row, s.seat_number) ORDER BY s.seat_row, s.seat_number SEPARATOR ', ') AS seats,
                       b.total_amount,
                       b.status,
                       b.booking_time
                FROM bookings b
                JOIN users u ON u.id = b.user_id
                JOIN booking_details bd ON bd.booking_id = b.id
                JOIN showtimes sh ON sh.id = bd.showtime_id
                JOIN movies m ON m.id = sh.movie_id
                JOIN rooms r ON r.id = sh.room_id
                JOIN cinemas c ON c.id = r.cinema_id
                JOIN seats s ON s.id = bd.seat_id
                """ + whereClause + """
                GROUP BY b.id, b.booking_code, u.username, m.title, c.name, r.name, sh.show_date, sh.start_time, b.total_amount, b.status, b.booking_time
                ORDER BY """ + orderBy;

        List<BookingSummary> bookings = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int parameterIndex = 1;
            if (userId != null) {
                preparedStatement.setInt(parameterIndex++, userId);
            }
            if (keyword != null) {
                String pattern = "%" + keyword + "%";
                preparedStatement.setString(parameterIndex++, pattern);
                preparedStatement.setString(parameterIndex++, pattern);
                preparedStatement.setString(parameterIndex++, pattern);
                preparedStatement.setString(parameterIndex, pattern);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp bookingTime = resultSet.getTimestamp("booking_time");
                    bookings.add(new BookingSummary(
                            resultSet.getInt("booking_id"),
                            resultSet.getString("booking_code"),
                            resultSet.getString("username"),
                            resultSet.getString("movie_title"),
                            resultSet.getString("cinema_name"),
                            resultSet.getString("room_name"),
                            resultSet.getDate("show_date").toLocalDate(),
                            resultSet.getTime("start_time").toLocalTime(),
                            resultSet.getString("seats"),
                            resultSet.getBigDecimal("total_amount"),
                            resultSet.getString("status"),
                            bookingTime == null ? null : bookingTime.toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach booking: " + e.getMessage());
        }

        return bookings;
    }

    private boolean cancelBooking(String bookingFilter, int bookingId, Integer userId) {
        String checkSql = "SELECT id FROM bookings " + bookingFilter;
        String bookingSql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";
        String bookingDetailSql = "UPDATE booking_details SET status = 'CANCELLED' WHERE booking_id = ?";
        String paymentSql = "UPDATE payments SET status = 'REFUNDED' WHERE booking_id = ?";

        try (Connection connection = JdbcUtil.getConnection()) {
            connection.setAutoCommit(false);

            try {
                if (!existsBookingForCancel(connection, checkSql, bookingId, userId)) {
                    connection.rollback();
                    return false;
                }

                try (PreparedStatement bookingStatement = connection.prepareStatement(bookingSql);
                     PreparedStatement detailStatement = connection.prepareStatement(bookingDetailSql);
                     PreparedStatement paymentStatement = connection.prepareStatement(paymentSql)) {
                    bookingStatement.setInt(1, bookingId);
                    bookingStatement.executeUpdate();

                    detailStatement.setInt(1, bookingId);
                    detailStatement.executeUpdate();

                    paymentStatement.setInt(1, bookingId);
                    paymentStatement.executeUpdate();
                }

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Loi khi huy booking: " + e.getMessage());
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Loi khi ket noi de huy booking: " + e.getMessage());
            return false;
        }
    }

    private BigDecimal fetchBasePrice(Connection connection, String sql, int showtimeId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, showtimeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("base_price");
                }
                return null;
            }
        }
    }

    private List<SeatAvailability> fetchSeatsForBooking(Connection connection, String sql, int showtimeId) throws SQLException {
        List<SeatAvailability> seats = new ArrayList<>();
        Set<Integer> bookedSeatIds = fetchBookedSeatIds(connection, showtimeId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, showtimeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int seatId = resultSet.getInt("seat_id");
                    seats.add(new SeatAvailability(
                            seatId,
                            resultSet.getString("seat_code"),
                            null,
                            resultSet.getBigDecimal("price_multiplier"),
                            bookedSeatIds.contains(seatId)
                    ));
                }
            }
        }

        return seats;
    }

    private Set<Integer> fetchBookedSeatIds(Connection connection, int showtimeId) throws SQLException {
        String sql = "SELECT seat_id FROM booking_details WHERE showtime_id = ? AND status = 'BOOKED'";
        Set<Integer> seatIds = new HashSet<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, showtimeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    seatIds.add(resultSet.getInt("seat_id"));
                }
            }
        }

        return seatIds;
    }

    private List<SeatAvailability> matchSelectedSeats(List<SeatAvailability> seats, List<String> seatCodes) {
        List<SeatAvailability> matchedSeats = new ArrayList<>();

        for (String seatCode : seatCodes) {
            for (SeatAvailability seat : seats) {
                if (seat.seatCode().equalsIgnoreCase(seatCode)) {
                    matchedSeats.add(seat);
                    break;
                }
            }
        }

        return matchedSeats;
    }

    private String generateBookingCode() {
        return "BK" + System.currentTimeMillis();
    }

    private boolean existsBookingForCancel(Connection connection, String sql, int bookingId, Integer userId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, bookingId);
            if (userId != null) {
                preparedStatement.setInt(2, userId);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
