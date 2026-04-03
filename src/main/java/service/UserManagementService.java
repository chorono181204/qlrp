package service;

import dao.RoleDAO;
import dao.UserDAO;
import model.Role;
import model.User;
import model.UserSummary;

import java.util.List;
import java.util.Set;

public class UserManagementService {
    private static final Set<String> ALLOWED_SORTS = Set.of("id", "full_name", "username", "role_name", "created_at");
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;

    public UserManagementService() {
        this.userDAO = new UserDAO();
        this.roleDAO = new RoleDAO();
    }

    public List<UserSummary> findAll(String sortBy) {
        return userDAO.findAll(normalizeSort(sortBy));
    }

    public List<UserSummary> search(String keyword, String sortBy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(sortBy);
        }
        return userDAO.search(keyword.trim(), normalizeSort(sortBy));
    }

    public List<Role> findAllRoles() {
        return roleDAO.findAll();
    }

    public String create(String fullName, String username, String password, String phone, String email, String roleName) {
        if (isBlank(fullName) || isBlank(username) || isBlank(password) || isBlank(roleName)) {
            return "Ho ten, username, password va role khong duoc de trong.";
        }

        if (username.trim().length() < 4) {
            return "Username phai co it nhat 4 ky tu.";
        }

        if (password.length() < 6) {
            return "Mat khau phai co it nhat 6 ky tu.";
        }

        if (userDAO.isUsernameExists(username.trim())) {
            return "Username da ton tai.";
        }

        User user = new User(fullName.trim(), username.trim(), password, phone, email, 0, roleName.trim().toUpperCase());
        return userDAO.createByAdmin(user) ? "Them user thanh cong." : "Them user that bai.";
    }

    public String update(String userIdText, String fullName, String username, String password,
                         String phone, String email, String roleName) {
        int userId;

        try {
            userId = Integer.parseInt(userIdText.trim());
        } catch (NumberFormatException e) {
            return "ID user phai la so nguyen.";
        }

        if (userId <= 0) {
            return "ID user khong hop le.";
        }

        if (isBlank(fullName) || isBlank(username) || isBlank(password) || isBlank(roleName)) {
            return "Ho ten, username, password va role khong duoc de trong.";
        }

        if (username.trim().length() < 4) {
            return "Username phai co it nhat 4 ky tu.";
        }

        if (password.length() < 6) {
            return "Mat khau phai co it nhat 6 ky tu.";
        }

        if (userDAO.existsByUsernameExceptId(username.trim(), userId)) {
            return "Username da ton tai.";
        }

        return userDAO.updateByAdmin(
                userId,
                fullName.trim(),
                username.trim(),
                password,
                phone,
                email,
                roleName.trim().toUpperCase()
        ) ? "Cap nhat user thanh cong." : "Cap nhat user that bai.";
    }

    public String delete(String userIdText) {
        int userId;

        try {
            userId = Integer.parseInt(userIdText.trim());
        } catch (NumberFormatException e) {
            return "ID user phai la so nguyen.";
        }

        if (userId <= 0) {
            return "ID user khong hop le.";
        }

        return userDAO.deleteById(userId) ? "Xoa user thanh cong." : "Xoa user that bai.";
    }

    private String normalizeSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "u.id";
        }

        String normalized = sortBy.trim().toLowerCase();
        if (!ALLOWED_SORTS.contains(normalized)) {
            return "u.id";
        }

        return switch (normalized) {
            case "full_name" -> "u.full_name";
            case "username" -> "u.username";
            case "role_name" -> "r.name";
            case "created_at" -> "u.created_at";
            default -> "u.id";
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
