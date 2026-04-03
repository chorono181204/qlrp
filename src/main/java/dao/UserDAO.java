package dao;

import model.User;
import model.UserSummary;
import utils.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String DEFAULT_CUSTOMER_ROLE = "CUSTOMER";

    public boolean isUsernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println("Loi khi kiem tra username: " + e.getMessage());
            return false;
        }
    }

    public boolean register(User user) {
        Integer roleId = findRoleIdByName(user.getRoleName());
        if (roleId == null) {
            System.err.println("Khong tim thay role mac dinh cho tai khoan moi.");
            return false;
        }

        String sql = """
                INSERT INTO users (full_name, username, password, phone, email, role_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getFullName());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getPhone());
            preparedStatement.setString(5, user.getEmail());
            preparedStatement.setInt(6, roleId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi dang ky tai khoan: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = """
                SELECT u.id, u.full_name, u.username, u.password, u.phone, u.email,
                       u.role_id, r.name AS role_name, u.created_at
                FROM users u
                JOIN roles r ON u.role_id = r.id
                WHERE u.username = ? AND u.password = ?
                """;

        try (Connection connection = JdbcUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi dang nhap: " + e.getMessage());
        }

        return null;
    }

    public List<UserSummary> findAll(String orderBy) {
        String sql = """
                SELECT u.id, u.full_name, u.username, u.phone, u.email, r.name AS role_name, u.created_at
                FROM users u
                JOIN roles r ON u.role_id = r.id
                ORDER BY """ + orderBy;

        List<UserSummary> users = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                users.add(new UserSummary(
                        resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("username"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("role_name"),
                        createdAt == null ? null : createdAt.toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach user: " + e.getMessage());
        }

        return users;
    }

    public List<UserSummary> search(String keyword, String orderBy) {
        String sql = """
                SELECT u.id, u.full_name, u.username, u.phone, u.email, r.name AS role_name, u.created_at
                FROM users u
                JOIN roles r ON u.role_id = r.id
                WHERE LOWER(u.full_name) LIKE LOWER(?)
                   OR LOWER(u.username) LIKE LOWER(?)
                   OR LOWER(r.name) LIKE LOWER(?)
                ORDER BY """ + orderBy;

        List<UserSummary> users = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            preparedStatement.setString(1, pattern);
            preparedStatement.setString(2, pattern);
            preparedStatement.setString(3, pattern);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp createdAt = resultSet.getTimestamp("created_at");
                    users.add(new UserSummary(
                            resultSet.getInt("id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("username"),
                            resultSet.getString("phone"),
                            resultSet.getString("email"),
                            resultSet.getString("role_name"),
                            createdAt == null ? null : createdAt.toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi tim kiem user: " + e.getMessage());
        }

        return users;
    }

    public boolean createByAdmin(User user) {
        return register(user);
    }

    public boolean updateByAdmin(int userId, String fullName, String username, String password,
                                 String phone, String email, String roleName) {
        Integer roleId = findRoleIdByName(roleName);
        if (roleId == null) {
            System.err.println("Khong tim thay role khi cap nhat user.");
            return false;
        }

        String sql = """
                UPDATE users
                SET full_name = ?, username = ?, password = ?, phone = ?, email = ?, role_id = ?
                WHERE id = ?
                """;

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, emptyToNull(phone));
            preparedStatement.setString(5, emptyToNull(email));
            preparedStatement.setInt(6, roleId);
            preparedStatement.setInt(7, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi cap nhat user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteById(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi khi xoa user: " + e.getMessage());
            return false;
        }
    }

    public boolean existsByUsernameExceptId(String username, int userId) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND id <> ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println("Loi khi kiem tra username khi cap nhat: " + e.getMessage());
            return false;
        }
    }

    private Integer findRoleIdByName(String roleName) {
        String sql = "SELECT id FROM roles WHERE name = ?";

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, roleName == null || roleName.isBlank() ? DEFAULT_CUSTOMER_ROLE : roleName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay role id: " + e.getMessage());
        }

        return null;
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");

        return new User(
                resultSet.getInt("id"),
                resultSet.getString("full_name"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("phone"),
                resultSet.getString("email"),
                resultSet.getInt("role_id"),
                resultSet.getString("role_name"),
                createdAt == null ? null : createdAt.toLocalDateTime()
        );
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
