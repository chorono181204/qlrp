package dao;

import model.Cinema;
import utils.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CinemaDAO {

    public List<Cinema> findAll(String whereClause, String orderBy, String keyword) {
        String sql = "SELECT id, name, address, phone FROM cinemas " + whereClause + " ORDER BY " + orderBy;
        List<Cinema> cinemas = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (keyword != null) {
                String pattern = "%" + keyword + "%";
                preparedStatement.setString(1, pattern);
                preparedStatement.setString(2, pattern);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cinemas.add(new Cinema(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("address"),
                            resultSet.getString("phone")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach rap: " + e.getMessage());
        }

        return cinemas;
    }

    public boolean create(String name, String address, String phone) {
        String sql = "INSERT INTO cinemas (name, address, phone) VALUES (?, ?, ?)";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, phone == null || phone.isBlank() ? null : phone.trim());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi them rap: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int id, String name, String address, String phone) {
        String sql = "UPDATE cinemas SET name = ?, address = ?, phone = ? WHERE id = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, phone == null || phone.isBlank() ? null : phone.trim());
            preparedStatement.setInt(4, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi cap nhat rap: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM cinemas WHERE id = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi xoa rap: " + e.getMessage());
            return false;
        }
    }
}
