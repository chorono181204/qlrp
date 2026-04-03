package service;

import dao.UserDAO;
import model.User;

public class AuthService {
    private static final String DEFAULT_CUSTOMER_ROLE = "CUSTOMER";
    private final UserDAO userDAO;
    private User currentUser;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public String register(String fullName, String username, String password, String confirmPassword) {
        if (isBlank(fullName) || isBlank(username) || isBlank(password) || isBlank(confirmPassword)) {
            return "Thong tin khong duoc de trong.";
        }

        if (username.length() < 4) {
            return "Username phai co it nhat 4 ky tu.";
        }

        if (password.length() < 6) {
            return "Mat khau phai co it nhat 6 ky tu.";
        }

        if (!password.equals(confirmPassword)) {
            return "Mat khau xac nhan khong khop.";
        }

        if (userDAO.isUsernameExists(username.trim())) {
            return "Username da ton tai.";
        }

        User user = new User(
                fullName.trim(),
                username.trim(),
                password,
                null,
                null,
                0,
                DEFAULT_CUSTOMER_ROLE
        );
        boolean isSuccess = userDAO.register(user);
        return isSuccess ? "Dang ky thanh cong." : "Dang ky that bai. Vui long kiem tra ket noi DB.";
    }

    public String login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return "Username va password khong duoc de trong.";
        }

        User user = userDAO.login(username.trim(), password);
        if (user == null) {
            return "Sai username hoac password.";
        }

        currentUser = user;
        return "Dang nhap thanh cong.";
    }

    public String logout() {
        if (currentUser == null) {
            return "Chua co tai khoan nao dang dang nhap.";
        }

        currentUser = null;
        return "Dang xuat thanh cong.";
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
