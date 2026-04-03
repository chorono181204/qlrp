import model.User;

public class ConsoleApplication {
    private final ConsoleContext context;
    private final AuthConsoleHandler authHandler;
    private final AdminConsoleHandler adminHandler;
    private final StaffConsoleHandler staffHandler;
    private final CustomerConsoleHandler customerHandler;

    public ConsoleApplication() {
        ConsolePrinter printer = new ConsolePrinter();
        this.context = new ConsoleContext();
        this.authHandler = new AuthConsoleHandler(context, printer);
        this.adminHandler = new AdminConsoleHandler(context, printer);
        this.staffHandler = new StaffConsoleHandler(context, printer);
        this.customerHandler = new CustomerConsoleHandler(context, printer);
    }

    public void start() {
        boolean isRunning = true;
        System.out.println(context.getAppTitle());

        while (isRunning) {
            if (!context.getAuthService().isLoggedIn()) {
                isRunning = authHandler.showGuestMenu();
            } else {
                showUserMenu();
            }
        }

        System.out.println("Da thoat chuong trinh.");
        context.getScanner().close();
    }

    private void showUserMenu() {
        User currentUser = context.getAuthService().getCurrentUser();
        String roleName = currentUser.getRoleName();

        if ("ADMIN".equalsIgnoreCase(roleName)) {
            adminHandler.showMenu(currentUser);
            return;
        }

        if ("STAFF".equalsIgnoreCase(roleName)) {
            staffHandler.showMenu(currentUser);
            return;
        }

        customerHandler.showMenu(currentUser);
    }
}
