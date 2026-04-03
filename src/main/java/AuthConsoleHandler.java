public class AuthConsoleHandler extends AbstractConsoleHandler {
    public AuthConsoleHandler(ConsoleContext context, ConsolePrinter printer) {
        super(context, printer);
    }

    public boolean showGuestMenu() {
        System.out.println("\n" + context.getAppTitle().toUpperCase());
        System.out.println("1. Dang ky");
        System.out.println("2. Dang nhap");
        System.out.println("0. Thoat");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> handleRegister();
            case "2" -> handleLogin();
            case "0" -> {
                return false;
            }
            default -> System.out.println("Lua chon khong hop le.");
        }
        return true;
    }

    private void handleRegister() {
        System.out.println("\nDANG KY TAI KHOAN");
        System.out.print("Ho va ten: ");
        String fullName = scanner().nextLine();
        System.out.print("Username: ");
        String username = scanner().nextLine();
        System.out.print("Password: ");
        String password = scanner().nextLine();
        System.out.print("Nhap lai password: ");
        String confirmPassword = scanner().nextLine();

        System.out.println(authService().register(fullName, username, password, confirmPassword));
    }

    private void handleLogin() {
        System.out.println("\nDANG NHAP");
        System.out.print("Username: ");
        String username = scanner().nextLine();
        System.out.print("Password: ");
        String password = scanner().nextLine();

        System.out.println(authService().login(username, password));
    }
}
