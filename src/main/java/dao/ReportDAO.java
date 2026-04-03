package dao;

import model.MovieRevenueSummary;
import model.ReportSummary;
import utils.JdbcUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public ReportSummary getSummary() {
        String sql = """
                SELECT
                    COUNT(*) AS total_bookings,
                    SUM(CASE WHEN status = 'PAID' THEN 1 ELSE 0 END) AS paid_bookings,
                    SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_bookings,
                    COALESCE(SUM(CASE WHEN status = 'PAID' THEN total_amount ELSE 0 END), 0) AS total_revenue
                FROM bookings
                """;
        String ticketSql = "SELECT COUNT(*) AS total_tickets_sold FROM booking_details WHERE status = 'BOOKED'";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement bookingStatement = connection.prepareStatement(sql);
             PreparedStatement ticketStatement = connection.prepareStatement(ticketSql);
             ResultSet bookingResult = bookingStatement.executeQuery();
             ResultSet ticketResult = ticketStatement.executeQuery()) {

            int totalBookings = 0;
            int paidBookings = 0;
            int cancelledBookings = 0;
            BigDecimal totalRevenue = BigDecimal.ZERO;
            int totalTicketsSold = 0;

            if (bookingResult.next()) {
                totalBookings = bookingResult.getInt("total_bookings");
                paidBookings = bookingResult.getInt("paid_bookings");
                cancelledBookings = bookingResult.getInt("cancelled_bookings");
                totalRevenue = bookingResult.getBigDecimal("total_revenue");
            }

            if (ticketResult.next()) {
                totalTicketsSold = ticketResult.getInt("total_tickets_sold");
            }

            return new ReportSummary(totalBookings, paidBookings, cancelledBookings, totalTicketsSold, totalRevenue);
        } catch (SQLException e) {
            System.err.println("Loi khi lay thong ke tong quan: " + e.getMessage());
            return new ReportSummary(0, 0, 0, 0, BigDecimal.ZERO);
        }
    }

    public List<MovieRevenueSummary> getTopMoviesByRevenue() {
        String sql = """
                SELECT m.title AS movie_title,
                       COUNT(bd.id) AS tickets_sold,
                       COALESCE(SUM(bd.ticket_price), 0) AS revenue
                FROM booking_details bd
                JOIN showtimes sh ON sh.id = bd.showtime_id
                JOIN movies m ON m.id = sh.movie_id
                WHERE bd.status = 'BOOKED'
                GROUP BY m.id, m.title
                ORDER BY revenue DESC, tickets_sold DESC, m.title
                LIMIT 5
                """;

        List<MovieRevenueSummary> movieReports = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                movieReports.add(new MovieRevenueSummary(
                        resultSet.getString("movie_title"),
                        resultSet.getInt("tickets_sold"),
                        resultSet.getBigDecimal("revenue")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay thong ke phim: " + e.getMessage());
        }

        return movieReports;
    }
}
