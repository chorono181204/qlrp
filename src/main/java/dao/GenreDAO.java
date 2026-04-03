package dao;

import model.Genre;
import utils.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {

    public List<Genre> findAll(String orderBy) {
        String sql = "SELECT id, name FROM genres ORDER BY " + orderBy;
        List<Genre> genres = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                genres.add(new Genre(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach the loai: " + e.getMessage());
        }

        return genres;
    }

    public List<Genre> search(String keyword, String orderBy) {
        String sql = "SELECT id, name FROM genres WHERE LOWER(name) LIKE LOWER(?) ORDER BY " + orderBy;
        List<Genre> genres = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + keyword + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    genres.add(new Genre(resultSet.getInt("id"), resultSet.getString("name")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi tim kiem the loai: " + e.getMessage());
        }

        return genres;
    }

    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM genres WHERE LOWER(name) = LOWER(?)";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println("Loi khi kiem tra the loai: " + e.getMessage());
            return false;
        }
    }

    public boolean create(String name) {
        String sql = "INSERT INTO genres (name) VALUES (?)";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi them the loai: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int id, String name) {
        String sql = "UPDATE genres SET name = ? WHERE id = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi cap nhat the loai: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM genres WHERE id = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi xoa the loai: " + e.getMessage());
            return false;
        }
    }
}
