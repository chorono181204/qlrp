import model.User;

public class CustomerConsoleHandler extends AbstractConsoleHandler {
    public CustomerConsoleHandler(ConsoleContext context, ConsolePrinter printer) {
        super(context, printer);
    }

    public void showMenu(User currentUser) {
        System.out.println("\nMENU KHACH HANG");
        System.out.println("Xin chao, " + currentUser.getFullName() + "!");
        System.out.println("1. Dat ve");
        System.out.println("2. Ve da dat");
        System.out.println("3. Dang xuat");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> handleCreateBooking(currentUser);
            case "2" -> showMyBookingsMenu(currentUser);
            case "3" -> System.out.println(authService().logout());
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

    private void showMyBookingsMenu(User currentUser) {
        System.out.println("\nVE DA DAT");
        printer.printBookingList(bookingService().findBookingsByUserId(currentUser.getId()), false);
        System.out.println("1. Huy ve");
        System.out.println("2. Tim kiem ve");
        System.out.println("3. Sap xep ve");
        System.out.println("0. Quay lai");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> handleCancelMyBooking(currentUser);
            case "2" -> handleSearchBookings(currentUser);
            case "3" -> handleSortBookings(currentUser);
            case "0" -> {
            }
            default -> System.out.println("Lua chon khong hop le.");
        }
    }

    private void handleCancelMyBooking(User currentUser) {
        printer.printBookingList(bookingService().findBookingsByUserId(currentUser.getId()), false);
        System.out.print("Nhap ID booking can huy: ");
        String bookingId = scanner().nextLine();
        System.out.println(bookingService().cancelBookingByUser(currentUser.getId(), bookingId));
    }

    private void handleSearchBookings(User currentUser) {
        System.out.print("Nhap tu khoa tim booking: ");
        String keyword = scanner().nextLine();
        printer.printBookingList(bookingService().searchBookings(keyword, "booking_time", currentUser.getId()), false);
    }

    private void handleSortBookings(User currentUser) {
        System.out.println("\nSAP XEP BOOKING");
        System.out.println("Nhap tieu chi: booking_time / booking_code / movie_title / cinema_name / total_amount / status");
        System.out.print("Tieu chi sap xep: ");
        String sortBy = scanner().nextLine();
        printer.printBookingList(bookingService().sortBookings(sortBy, currentUser.getId()), false);
    }
}
