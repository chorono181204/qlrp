import model.User;

public class StaffConsoleHandler extends AbstractConsoleHandler {
    public StaffConsoleHandler(ConsoleContext context, ConsolePrinter printer) {
        super(context, printer);
    }

    public void showMenu(User currentUser) {
        System.out.println("\nMENU NHAN VIEN");
        System.out.println("Xin chao, " + currentUser.getFullName() + "!");
        System.out.println("1. Xem thong tin ca nhan");
        System.out.println("2. Xem danh sach phim");
        System.out.println("3. Xem lich chieu");
        System.out.println("4. Ban ve");
        System.out.println("5. Xem tat ca booking");
        System.out.println("6. Tim kiem booking");
        System.out.println("7. Sap xep booking");
        System.out.println("8. Huy booking");
        System.out.println("9. Dang xuat");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> printer.showCurrentUserInfo(currentUser);
            case "2" -> printer.printMovieList(movieService().findAll("id"));
            case "3" -> printer.printShowtimeList(showtimeService().findAll("id"));
            case "4" -> handleCreateBooking(currentUser);
            case "5" -> printer.printBookingList(bookingService().findAllBookings(), true);
            case "6" -> handleSearchBookings();
            case "7" -> handleSortBookings();
            case "8" -> handleCancelAnyBooking();
            case "9" -> System.out.println(authService().logout());
            default -> System.out.println("Lua chon khong hop le.");
        }
    }

    private void handleCreateBooking(User currentUser) {
        System.out.println("\nDAT VE");
        printer.printShowtimeList(showtimeService().findAll("id"));
        System.out.print("ID lich chieu: ");
        String showtimeId = scanner().nextLine();
        printer.printSeatsByShowtime(bookingService().findSeatsByShowtime(showtimeId));
        System.out.print("Nhap ma ghe, cach nhau boi dau phay (vi du A1,A2): ");
        String seatCodes = scanner().nextLine();
        System.out.print("Phuong thuc thanh toan (CASH/CARD/MOMO): ");
        String paymentMethod = scanner().nextLine();

        System.out.println(bookingService().createBooking(currentUser.getId(), showtimeId, seatCodes, paymentMethod));
    }

    private void handleSearchBookings() {
        System.out.print("Nhap tu khoa tim booking: ");
        String keyword = scanner().nextLine();
        printer.printBookingList(bookingService().searchBookings(keyword, "booking_time", null), true);
    }

    private void handleSortBookings() {
        System.out.println("\nSAP XEP BOOKING");
        System.out.println("Nhap tieu chi: booking_time / booking_code / movie_title / cinema_name / total_amount / status");
        System.out.print("Tieu chi sap xep: ");
        String sortBy = scanner().nextLine();
        printer.printBookingList(bookingService().sortBookings(sortBy, null), true);
    }

    private void handleCancelAnyBooking() {
        printer.printBookingList(bookingService().findAllBookings(), true);
        System.out.print("Nhap ID booking can huy: ");
        String bookingId = scanner().nextLine();
        System.out.println(bookingService().cancelBooking(bookingId));
    }
}
