import model.BookingSummary;
import model.Cinema;
import model.Genre;
import model.MovieRevenueSummary;
import model.MovieSummary;
import model.ReportSummary;
import model.Role;
import model.RoomSummary;
import model.SeatAvailability;
import model.SeatSummary;
import model.SeatType;
import model.ShowtimeSummary;
import model.User;
import model.UserSummary;

import java.util.List;

public class ConsolePrinter {
    public void showCurrentUserInfo(User user) {
        System.out.println("\nTHONG TIN CA NHAN");
        System.out.println("ID: " + user.getId());
        System.out.println("Ho va ten: " + user.getFullName());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Role: " + user.getRoleName());
    }

    public void printRoleList(List<Role> roles) {
        System.out.println("\nDANH SACH ROLE");
        for (Role role : roles) {
            System.out.println(role.id() + ". " + role.name());
        }
    }

    public void printUserList(List<UserSummary> users) {
        System.out.println("\nDANH SACH USER");
        if (users.isEmpty()) {
            System.out.println("Chua co user nao.");
            return;
        }
        for (UserSummary user : users) {
            String phone = user.phone() == null || user.phone().isBlank() ? "Chua cap nhat" : user.phone();
            String email = user.email() == null || user.email().isBlank() ? "Chua cap nhat" : user.email();
            System.out.println(user.id() + ". " + user.fullName());
            System.out.println("   Username: " + user.username());
            System.out.println("   Phone: " + phone);
            System.out.println("   Email: " + email);
            System.out.println("   Role: " + user.roleName());
            System.out.println("   Created at: " + user.createdAt());
        }
    }

    public void printGenreList(List<Genre> genres) {
        System.out.println("\nDANH SACH THE LOAI");
        if (genres.isEmpty()) {
            System.out.println("Khong co the loai nao phu hop.");
            return;
        }
        for (Genre genre : genres) {
            System.out.println(genre.id() + ". " + genre.name());
        }
    }

    public void printMovieList(List<MovieSummary> movies) {
        System.out.println("\nDANH SACH PHIM");
        if (movies.isEmpty()) {
            System.out.println("Khong co phim nao phu hop.");
            return;
        }
        for (MovieSummary movie : movies) {
            String director = movie.director().isBlank() ? "Chua cap nhat" : movie.director();
            String genres = movie.genres().isBlank() ? "Chua gan the loai" : movie.genres();
            System.out.println(movie.id() + ". " + movie.title());
            System.out.println("   Thoi luong: " + movie.durationMinutes() + " phut");
            System.out.println("   Dao dien: " + director);
            System.out.println("   Trang thai: " + movie.status());
            System.out.println("   The loai: " + genres);
        }
    }

    public void printCinemaList(List<Cinema> cinemas) {
        System.out.println("\nDANH SACH RAP");
        if (cinemas.isEmpty()) {
            System.out.println("Khong co rap nao phu hop.");
            return;
        }
        for (Cinema cinema : cinemas) {
            String phone = cinema.phone() == null || cinema.phone().isBlank() ? "Chua cap nhat" : cinema.phone();
            System.out.println(cinema.id() + ". " + cinema.name());
            System.out.println("   Dia chi: " + cinema.address());
            System.out.println("   So dien thoai: " + phone);
        }
    }

    public void printRoomList(List<RoomSummary> rooms) {
        System.out.println("\nDANH SACH PHONG");
        if (rooms.isEmpty()) {
            System.out.println("Khong co phong nao phu hop.");
            return;
        }
        for (RoomSummary room : rooms) {
            System.out.println(room.id() + ". " + room.roomName());
            System.out.println("   Rap: " + room.cinemaName());
            System.out.println("   Suc chua: " + room.capacity());
            System.out.println("   Loai phong: " + room.roomType());
            System.out.println("   Trang thai: " + room.status());
        }
    }

    public void printSeatTypeList(List<SeatType> seatTypes) {
        System.out.println("\nDANH SACH LOAI GHE");
        for (SeatType seatType : seatTypes) {
            System.out.println(seatType.id() + ". " + seatType.name() + " (he so gia: " + seatType.priceMultiplier() + ")");
        }
    }

    public void printSeatList(List<SeatSummary> seats) {
        System.out.println("\nDANH SACH GHE");
        if (seats.isEmpty()) {
            System.out.println("Chua co ghe nao hoac du lieu tim kiem khong hop le.");
            return;
        }
        for (SeatSummary seat : seats) {
            System.out.println(seat.id() + ". " + seat.seatCode());
            System.out.println("   Phong: " + seat.roomName());
            System.out.println("   Loai ghe: " + seat.seatTypeName());
            System.out.println("   Trang thai: " + seat.status());
        }
    }

    public void printShowtimeList(List<ShowtimeSummary> showtimes) {
        System.out.println("\nDANH SACH LICH CHIEU");
        if (showtimes.isEmpty()) {
            System.out.println("Khong co lich chieu nao phu hop.");
            return;
        }
        for (ShowtimeSummary showtime : showtimes) {
            System.out.println(showtime.id() + ". " + showtime.movieTitle());
            System.out.println("   Rap: " + showtime.cinemaName());
            System.out.println("   Phong: " + showtime.roomName());
            System.out.println("   Ngay chieu: " + showtime.showDate());
            System.out.println("   Gio: " + showtime.startTime() + " - " + showtime.endTime());
            System.out.println("   Gia co ban: " + showtime.basePrice());
            System.out.println("   Trang thai: " + showtime.status());
        }
    }

    public void printSeatsByShowtime(List<SeatAvailability> seats) {
        System.out.println("\nDANH SACH GHE THEO LICH CHIEU");
        if (seats.isEmpty()) {
            System.out.println("Khong tim thay lich chieu hoac chua co ghe.");
            return;
        }
        for (SeatAvailability seat : seats) {
            String status = seat.booked() ? "Da dat" : "Trong";
            System.out.println(seat.seatCode() + " - " + status + " - " + seat.seatTypeName() + " - he so " + seat.priceMultiplier());
        }
    }

    public void printBookingList(List<BookingSummary> bookings, boolean showUsername) {
        System.out.println("\nDANH SACH BOOKING");
        if (bookings.isEmpty()) {
            System.out.println("Chua co booking nao.");
            return;
        }
        for (BookingSummary booking : bookings) {
            System.out.println(booking.bookingId() + ". " + booking.bookingCode());
            if (showUsername) {
                System.out.println("   Tai khoan: " + booking.username());
            }
            System.out.println("   Phim: " + booking.movieTitle());
            System.out.println("   Rap: " + booking.cinemaName());
            System.out.println("   Phong: " + booking.roomName());
            System.out.println("   Ngay chieu: " + booking.showDate());
            System.out.println("   Gio bat dau: " + booking.startTime());
            System.out.println("   Ghe: " + booking.seats());
            System.out.println("   Tong tien: " + booking.totalAmount());
            System.out.println("   Trang thai: " + booking.status());
            System.out.println("   Thoi gian dat: " + booking.bookingTime());
        }
    }

    public void printReport(ReportSummary summary, List<MovieRevenueSummary> topMovies) {
        System.out.println("\nTHONG KE");
        System.out.println("Tong so booking: " + summary.totalBookings());
        System.out.println("Booking da thanh toan: " + summary.paidBookings());
        System.out.println("Booking da huy: " + summary.cancelledBookings());
        System.out.println("Tong so ve da ban: " + summary.totalTicketsSold());
        System.out.println("Tong doanh thu: " + summary.totalRevenue());

        System.out.println("\nTOP PHIM DOANH THU");
        if (topMovies.isEmpty()) {
            System.out.println("Chua co du lieu doanh thu.");
            return;
        }
        for (int i = 0; i < topMovies.size(); i++) {
            MovieRevenueSummary movie = topMovies.get(i);
            System.out.println((i + 1) + ". " + movie.movieTitle());
            System.out.println("   So ve ban: " + movie.ticketsSold());
            System.out.println("   Doanh thu: " + movie.revenue());
        }
    }
}
