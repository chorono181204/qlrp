import model.Role;
import model.SeatType;
import model.User;

import java.util.List;

public class AdminConsoleHandler extends AbstractConsoleHandler {
    public AdminConsoleHandler(ConsoleContext context, ConsolePrinter printer) {
        super(context, printer);
    }

    public void showMenu(User currentUser) {
        System.out.println("\nMENU ADMIN");
        System.out.println("Xin chao, " + currentUser.getFullName() + "!");
        System.out.println("1. Xem thong tin ca nhan");
        System.out.println("2. Quan ly nguoi dung");
        System.out.println("3. Quan ly the loai");
        System.out.println("4. Quan ly phim");
        System.out.println("5. Quan ly rap");
        System.out.println("6. Quan ly phong");
        System.out.println("7. Quan ly ghe");
        System.out.println("8. Quan ly lich chieu");
        System.out.println("9. Thong ke");
        System.out.println("10. Dang xuat");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> printer.showCurrentUserInfo(currentUser);
            case "2" -> showUserManagementMenu();
            case "3" -> showGenreManagementMenu();
            case "4" -> showMovieManagementMenu();
            case "5" -> showCinemaManagementMenu();
            case "6" -> showRoomManagementMenu();
            case "7" -> showSeatManagementMenu();
            case "8" -> showShowtimeManagementMenu();
            case "9" -> printer.printReport(reportService().getSummary(), reportService().getTopMoviesByRevenue());
            case "10" -> System.out.println(authService().logout());
            default -> System.out.println("Lua chon khong hop le.");
        }
    }

    private void showUserManagementMenu() {
        System.out.println("\nQUAN LY NGUOI DUNG");
        System.out.println("1. Xem danh sach user");
        System.out.println("2. Them user");
        System.out.println("3. Sua user");
        System.out.println("4. Xoa user");
        System.out.println("5. Tim kiem user");
        System.out.println("6. Sap xep user");
        System.out.println("0. Quay lai");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> printer.printUserList(userManagementService().findAll("id"));
            case "2" -> handleCreateUser();
            case "3" -> handleUpdateUser();
            case "4" -> handleDeleteUser();
            case "5" -> handleSearchUsers();
            case "6" -> handleSortUsers();
            case "0" -> {
            }
            default -> System.out.println("Lua chon khong hop le.");
        }
    }

    private void showGenreManagementMenu() {
        System.out.println("\nQUAN LY THE LOAI");
        System.out.println("1. Xem danh sach the loai");
        System.out.println("2. Them the loai");
        System.out.println("3. Sua the loai");
        System.out.println("4. Xoa the loai");
        System.out.println("5. Tim kiem the loai");
        System.out.println("6. Sap xep the loai");
        System.out.println("0. Quay lai");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> printer.printGenreList(genreService().findAll("id"));
            case "2" -> handleCreateGenre();
            case "3" -> handleUpdateGenre();
            case "4" -> handleDeleteGenre();
            case "5" -> handleSearchGenres();
            case "6" -> handleSortGenres();
            case "0" -> {
            }
            default -> System.out.println("Lua chon khong hop le.");
        }
    }

    private void showMovieManagementMenu() {
        System.out.println("\nQUAN LY PHIM");
        System.out.println("1. Xem danh sach phim");
        System.out.println("2. Them phim");
        System.out.println("3. Sua phim");
        System.out.println("4. Xoa phim");
        System.out.println("5. Tim kiem phim");
        System.out.println("6. Sap xep phim");
        System.out.println("0. Quay lai");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> printer.printMovieList(movieService().findAll("id"));
            case "2" -> handleCreateMovie();
            case "3" -> handleUpdateMovie();
            case "4" -> handleDeleteMovie();
            case "5" -> handleSearchMovies();
            case "6" -> handleSortMovies();
            case "0" -> {
            }
            default -> System.out.println("Lua chon khong hop le.");
        }
    }

    private void showCinemaManagementMenu() {
        System.out.println("\nQUAN LY RAP");
        System.out.println("1. Xem danh sach rap");
        System.out.println("2. Them rap");
        System.out.println("3. Sua rap");
        System.out.println("4. Tim kiem rap");
        System.out.println("5. Sap xep rap");
        System.out.println("6. Xoa rap");
        System.out.println("0. Quay lai");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> printer.printCinemaList(cinemaService().findAll("id"));
            case "2" -> handleCreateCinema();
            case "3" -> handleUpdateCinema();
            case "4" -> handleSearchCinemas();
            case "5" -> handleSortCinemas();
            case "6" -> handleDeleteCinema();
            case "0" -> {
            }
            default -> System.out.println("Lua chon khong hop le.");
        }
    }

    private void showRoomManagementMenu() {
        System.out.println("\nQUAN LY PHONG");
        System.out.println("1. Xem danh sach phong");
        System.out.println("2. Them phong");
        System.out.println("3. Sua phong");
        System.out.println("4. Tim kiem phong");
        System.out.println("5. Sap xep phong");
        System.out.println("6. Xoa phong");
        System.out.println("0. Quay lai");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> printer.printRoomList(roomService().findAll("id"));
            case "2" -> handleCreateRoom();
            case "3" -> handleUpdateRoom();
            case "4" -> handleSearchRooms();
            case "5" -> handleSortRooms();
            case "6" -> handleDeleteRoom();
            case "0" -> {
            }
            default -> System.out.println("Lua chon khong hop le.");
        }
    }

    private void showSeatManagementMenu() {
        System.out.println("\nQUAN LY GHE");
        System.out.println("1. Xem danh sach ghe theo phong");
        System.out.println("2. Tao ghe hang loat cho phong");
        System.out.println("3. Tim kiem ghe theo phong");
        System.out.println("4. Sap xep ghe theo phong");
        System.out.println("0. Quay lai");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> handleViewSeatsByRoom();
            case "2" -> handleGenerateSeats();
            case "3" -> handleSearchSeatsByRoom();
            case "4" -> handleSortSeatsByRoom();
            case "0" -> {
            }
            default -> System.out.println("Lua chon khong hop le.");
        }
    }

    private void showShowtimeManagementMenu() {
        System.out.println("\nQUAN LY LICH CHIEU");
        System.out.println("1. Xem danh sach lich chieu");
        System.out.println("2. Them lich chieu");
        System.out.println("3. Sua lich chieu");
        System.out.println("4. Xoa lich chieu");
        System.out.println("5. Tim kiem lich chieu");
        System.out.println("6. Sap xep lich chieu");
        System.out.println("0. Quay lai");
        System.out.print("Chon chuc nang: ");

        String choice = scanner().nextLine().trim();
        switch (choice) {
            case "1" -> printer.printShowtimeList(showtimeService().findAll("id"));
            case "2" -> handleCreateShowtime();
            case "3" -> handleUpdateShowtime();
            case "4" -> handleDeleteShowtime();
            case "5" -> handleSearchShowtimes();
            case "6" -> handleSortShowtimes();
            case "0" -> {
            }
            default -> System.out.println("Lua chon khong hop le.");
        }
    }

    private void handleCreateGenre() {
        System.out.println("\nTHEM THE LOAI");
        System.out.print("Ten the loai: ");
        String genreName = scanner().nextLine();
        System.out.println(genreService().create(genreName));
    }

    private void handleUpdateGenre() {
        System.out.println("\nSUA THE LOAI");
        printer.printGenreList(genreService().findAll("id"));
        System.out.print("Nhap ID the loai can sua: ");
        String genreId = scanner().nextLine();
        System.out.print("Nhap ten the loai moi: ");
        String genreName = scanner().nextLine();
        System.out.println(genreService().update(genreId, genreName));
    }

    private void handleDeleteGenre() {
        printer.printGenreList(genreService().findAll("id"));
        System.out.print("Nhap ID the loai can xoa: ");
        String genreIdText = scanner().nextLine();

        try {
            int genreId = Integer.parseInt(genreIdText.trim());
            System.out.println(genreService().delete(genreId));
        } catch (NumberFormatException e) {
            System.out.println("ID the loai phai la so nguyen.");
        }
    }

    private void handleSearchGenres() {
        System.out.print("Nhap tu khoa tim the loai: ");
        String keyword = scanner().nextLine();
        printer.printGenreList(genreService().search(keyword, "id"));
    }

    private void handleSortGenres() {
        System.out.println("\nSAP XEP THE LOAI");
        System.out.println("Nhap tieu chi: id / name");
        System.out.print("Tieu chi sap xep: ");
        String sortBy = scanner().nextLine();
        printer.printGenreList(genreService().findAll(sortBy));
    }

    private void handleCreateMovie() {
        System.out.println("\nTHEM PHIM");
        printer.printGenreList(genreService().findAll("id"));
        System.out.print("Ten phim: ");
        String title = scanner().nextLine();
        System.out.print("Thoi luong (phut): ");
        String durationMinutes = scanner().nextLine();
        System.out.print("Dao dien: ");
        String director = scanner().nextLine();
        System.out.print("Ngon ngu: ");
        String language = scanner().nextLine();
        System.out.print("Do tuoi: ");
        String ageRating = scanner().nextLine();
        System.out.print("Mo ta: ");
        String description = scanner().nextLine();
        System.out.print("Trang thai (COMING_SOON/NOW_SHOWING/STOPPED): ");
        String status = scanner().nextLine();
        System.out.print("Nhap ID the loai, cach nhau boi dau phay (co the bo trong): ");
        String genreIds = scanner().nextLine();

        System.out.println(movieService().create(
                title,
                durationMinutes,
                director,
                language,
                ageRating,
                description,
                status,
                genreIds
        ));
    }

    private void handleDeleteMovie() {
        printer.printMovieList(movieService().findAll("id"));
        System.out.print("Nhap ID phim can xoa: ");
        String movieId = scanner().nextLine();
        System.out.println(movieService().delete(movieId));
    }

    private void handleUpdateMovie() {
        System.out.println("\nSUA PHIM");
        printer.printMovieList(movieService().findAll("id"));
        printer.printGenreList(genreService().findAll("id"));
        System.out.print("ID phim: ");
        String movieId = scanner().nextLine();
        System.out.print("Ten phim: ");
        String title = scanner().nextLine();
        System.out.print("Thoi luong (phut): ");
        String durationMinutes = scanner().nextLine();
        System.out.print("Dao dien: ");
        String director = scanner().nextLine();
        System.out.print("Ngon ngu: ");
        String language = scanner().nextLine();
        System.out.print("Do tuoi: ");
        String ageRating = scanner().nextLine();
        System.out.print("Mo ta: ");
        String description = scanner().nextLine();
        System.out.print("Trang thai (COMING_SOON/NOW_SHOWING/STOPPED): ");
        String status = scanner().nextLine();
        System.out.print("Nhap ID the loai, cach nhau boi dau phay: ");
        String genreIds = scanner().nextLine();

        System.out.println(movieService().update(
                movieId,
                title,
                durationMinutes,
                director,
                language,
                ageRating,
                description,
                status,
                genreIds
        ));
    }

    private void handleSearchMovies() {
        System.out.print("Nhap tu khoa tim phim: ");
        String keyword = scanner().nextLine();
        printer.printMovieList(movieService().search(keyword, "id"));
    }

    private void handleSortMovies() {
        System.out.println("\nSAP XEP PHIM");
        System.out.println("Nhap tieu chi: id / title / duration_minutes / status");
        System.out.print("Tieu chi sap xep: ");
        String sortBy = scanner().nextLine();
        printer.printMovieList(movieService().findAll(sortBy));
    }

    private void handleCreateCinema() {
        System.out.println("\nTHEM RAP");
        System.out.print("Ten rap: ");
        String name = scanner().nextLine();
        System.out.print("Dia chi: ");
        String address = scanner().nextLine();
        System.out.print("So dien thoai: ");
        String phone = scanner().nextLine();
        System.out.println(cinemaService().create(name, address, phone));
    }

    private void handleUpdateCinema() {
        System.out.println("\nSUA RAP");
        printer.printCinemaList(cinemaService().findAll("id"));
        System.out.print("ID rap: ");
        String cinemaId = scanner().nextLine();
        System.out.print("Ten rap moi: ");
        String name = scanner().nextLine();
        System.out.print("Dia chi moi: ");
        String address = scanner().nextLine();
        System.out.print("So dien thoai moi: ");
        String phone = scanner().nextLine();
        System.out.println(cinemaService().update(cinemaId, name, address, phone));
    }

    private void handleDeleteCinema() {
        printer.printCinemaList(cinemaService().findAll("id"));
        System.out.print("Nhap ID rap can xoa: ");
        String cinemaId = scanner().nextLine();
        System.out.println(cinemaService().delete(cinemaId));
    }

    private void handleSearchCinemas() {
        System.out.print("Nhap tu khoa tim rap: ");
        String keyword = scanner().nextLine();
        printer.printCinemaList(cinemaService().search(keyword, "id"));
    }

    private void handleSortCinemas() {
        System.out.println("\nSAP XEP RAP");
        System.out.println("Nhap tieu chi: id / name / address");
        System.out.print("Tieu chi sap xep: ");
        String sortBy = scanner().nextLine();
        printer.printCinemaList(cinemaService().findAll(sortBy));
    }

    private void handleCreateRoom() {
        System.out.println("\nTHEM PHONG");
        printer.printCinemaList(cinemaService().findAll("id"));
        System.out.print("ID rap: ");
        String cinemaId = scanner().nextLine();
        System.out.print("Ten phong: ");
        String roomName = scanner().nextLine();
        System.out.print("Suc chua: ");
        String capacity = scanner().nextLine();
        System.out.print("Loai phong (2D/3D/IMAX/4DX): ");
        String roomType = scanner().nextLine();
        System.out.println(roomService().create(cinemaId, roomName, capacity, roomType));
    }

    private void handleUpdateRoom() {
        System.out.println("\nSUA PHONG");
        printer.printRoomList(roomService().findAll("id"));
        printer.printCinemaList(cinemaService().findAll("id"));
        System.out.print("ID phong: ");
        String roomId = scanner().nextLine();
        System.out.print("ID rap: ");
        String cinemaId = scanner().nextLine();
        System.out.print("Ten phong moi: ");
        String roomName = scanner().nextLine();
        System.out.print("Suc chua moi: ");
        String capacity = scanner().nextLine();
        System.out.print("Loai phong moi (2D/3D/IMAX/4DX): ");
        String roomType = scanner().nextLine();
        System.out.println(roomService().update(roomId, cinemaId, roomName, capacity, roomType));
    }

    private void handleDeleteRoom() {
        printer.printRoomList(roomService().findAll("id"));
        System.out.print("Nhap ID phong can xoa: ");
        String roomId = scanner().nextLine();
        System.out.println(roomService().delete(roomId));
    }

    private void handleSearchRooms() {
        System.out.print("Nhap tu khoa tim phong: ");
        String keyword = scanner().nextLine();
        printer.printRoomList(roomService().search(keyword, "id"));
    }

    private void handleSortRooms() {
        System.out.println("\nSAP XEP PHONG");
        System.out.println("Nhap tieu chi: id / cinema_name / room_name / capacity / room_type");
        System.out.print("Tieu chi sap xep: ");
        String sortBy = scanner().nextLine();
        printer.printRoomList(roomService().findAll(sortBy));
    }

    private void handleViewSeatsByRoom() {
        printer.printRoomList(roomService().findAll("id"));
        System.out.print("Nhap ID phong can xem ghe: ");
        String roomId = scanner().nextLine();
        printer.printSeatList(seatService().findByRoomId(roomId));
    }

    private void handleGenerateSeats() {
        System.out.println("\nTAO GHE HANG LOAT");
        printer.printRoomList(roomService().findAll("id"));
        printSeatTypeList();
        System.out.print("ID phong: ");
        String roomId = scanner().nextLine();
        System.out.print("So hang ghe: ");
        String rowCount = scanner().nextLine();
        System.out.print("So ghe moi hang: ");
        String seatsPerRow = scanner().nextLine();
        System.out.print("ID loai ghe: ");
        String seatTypeId = scanner().nextLine();

        System.out.println(seatService().generateSeats(roomId, rowCount, seatsPerRow, seatTypeId));
    }

    private void handleSearchSeatsByRoom() {
        printer.printRoomList(roomService().findAll("id"));
        System.out.print("Nhap ID phong: ");
        String roomId = scanner().nextLine();
        System.out.print("Nhap tu khoa tim ghe: ");
        String keyword = scanner().nextLine();
        printer.printSeatList(seatService().searchByRoomId(roomId, keyword, "seat_code"));
    }

    private void handleSortSeatsByRoom() {
        printer.printRoomList(roomService().findAll("id"));
        System.out.print("Nhap ID phong: ");
        String roomId = scanner().nextLine();
        System.out.println("Nhap tieu chi: seat_code / seat_type_name / status");
        System.out.print("Tieu chi sap xep: ");
        String sortBy = scanner().nextLine();
        printer.printSeatList(seatService().sortByRoomId(roomId, sortBy));
    }

    private void handleCreateShowtime() {
        System.out.println("\nTHEM LICH CHIEU");
        printer.printMovieList(movieService().findAll("id"));
        printer.printRoomList(roomService().findAll("id"));
        System.out.print("ID phim: ");
        String movieId = scanner().nextLine();
        System.out.print("ID phong: ");
        String roomId = scanner().nextLine();
        System.out.print("Ngay chieu (YYYY-MM-DD): ");
        String showDate = scanner().nextLine();
        System.out.print("Gio bat dau (HH:MM): ");
        String startTime = scanner().nextLine();
        System.out.print("Gio ket thuc (HH:MM): ");
        String endTime = scanner().nextLine();
        System.out.print("Gia ve co ban: ");
        String basePrice = scanner().nextLine();
        System.out.print("Trang thai (OPEN/CLOSED/CANCELLED): ");
        String status = scanner().nextLine();

        System.out.println(showtimeService().create(
                movieId,
                roomId,
                showDate,
                startTime,
                endTime,
                basePrice,
                status
        ));
    }

    private void handleDeleteShowtime() {
        printer.printShowtimeList(showtimeService().findAll("id"));
        System.out.print("Nhap ID lich chieu can xoa: ");
        String showtimeId = scanner().nextLine();
        System.out.println(showtimeService().delete(showtimeId));
    }

    private void handleUpdateShowtime() {
        System.out.println("\nSUA LICH CHIEU");
        printer.printShowtimeList(showtimeService().findAll("id"));
        printer.printMovieList(movieService().findAll("id"));
        printer.printRoomList(roomService().findAll("id"));
        System.out.print("ID lich chieu: ");
        String showtimeId = scanner().nextLine();
        System.out.print("ID phim: ");
        String movieId = scanner().nextLine();
        System.out.print("ID phong: ");
        String roomId = scanner().nextLine();
        System.out.print("Ngay chieu (YYYY-MM-DD): ");
        String showDate = scanner().nextLine();
        System.out.print("Gio bat dau (HH:MM): ");
        String startTime = scanner().nextLine();
        System.out.print("Gio ket thuc (HH:MM): ");
        String endTime = scanner().nextLine();
        System.out.print("Gia ve co ban: ");
        String basePrice = scanner().nextLine();
        System.out.print("Trang thai (OPEN/CLOSED/CANCELLED): ");
        String status = scanner().nextLine();

        System.out.println(showtimeService().update(
                showtimeId,
                movieId,
                roomId,
                showDate,
                startTime,
                endTime,
                basePrice,
                status
        ));
    }

    private void handleSearchShowtimes() {
        System.out.print("Nhap tu khoa tim lich chieu: ");
        String keyword = scanner().nextLine();
        printer.printShowtimeList(showtimeService().search(keyword, "id"));
    }

    private void handleSortShowtimes() {
        System.out.println("\nSAP XEP LICH CHIEU");
        System.out.println("Nhap tieu chi: id / show_date / start_time / movie_title / cinema_name / room_name / status");
        System.out.print("Tieu chi sap xep: ");
        String sortBy = scanner().nextLine();
        printer.printShowtimeList(showtimeService().findAll(sortBy));
    }

    private void handleCreateUser() {
        System.out.println("\nTHEM USER");
        printRoleList();
        System.out.print("Ho va ten: ");
        String fullName = scanner().nextLine();
        System.out.print("Username: ");
        String username = scanner().nextLine();
        System.out.print("Password: ");
        String password = scanner().nextLine();
        System.out.print("Phone: ");
        String phone = scanner().nextLine();
        System.out.print("Email: ");
        String email = scanner().nextLine();
        System.out.print("Role (ADMIN/STAFF/CUSTOMER): ");
        String role = scanner().nextLine();

        System.out.println(userManagementService().create(fullName, username, password, phone, email, role));
    }

    private void handleUpdateUser() {
        System.out.println("\nSUA USER");
        printer.printUserList(userManagementService().findAll("id"));
        printRoleList();
        System.out.print("ID user: ");
        String userId = scanner().nextLine();
        System.out.print("Ho va ten moi: ");
        String fullName = scanner().nextLine();
        System.out.print("Username moi: ");
        String username = scanner().nextLine();
        System.out.print("Password moi: ");
        String password = scanner().nextLine();
        System.out.print("Phone moi: ");
        String phone = scanner().nextLine();
        System.out.print("Email moi: ");
        String email = scanner().nextLine();
        System.out.print("Role moi (ADMIN/STAFF/CUSTOMER): ");
        String role = scanner().nextLine();

        System.out.println(userManagementService().update(userId, fullName, username, password, phone, email, role));
    }

    private void handleDeleteUser() {
        printer.printUserList(userManagementService().findAll("id"));
        System.out.print("Nhap ID user can xoa: ");
        String userId = scanner().nextLine();
        System.out.println(userManagementService().delete(userId));
    }

    private void handleSearchUsers() {
        System.out.print("Nhap tu khoa tim user: ");
        String keyword = scanner().nextLine();
        printer.printUserList(userManagementService().search(keyword, "id"));
    }

    private void handleSortUsers() {
        System.out.println("\nSAP XEP USER");
        System.out.println("Nhap tieu chi: id / full_name / username / role_name / created_at");
        System.out.print("Tieu chi sap xep: ");
        String sortBy = scanner().nextLine();
        printer.printUserList(userManagementService().findAll(sortBy));
    }

    private void printRoleList() {
        List<Role> roles = userManagementService().findAllRoles();
        printer.printRoleList(roles);
    }

    private void printSeatTypeList() {
        List<SeatType> seatTypes = seatService().findSeatTypes();
        printer.printSeatTypeList(seatTypes);
    }
}
