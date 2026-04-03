package dao;

import model.Role;
import utils.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<Role> findAll() {
        String sql = "SELECT id, name FROM roles ORDER BY id";
        List<Role> roles = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                roles.add(new Role(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Loi khi lay danh sach role: " + e.getMessage());
        }

        return roles;
    }
}
