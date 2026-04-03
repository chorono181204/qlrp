import service.AuthService;
import service.BookingService;
import service.CinemaService;
import service.GenreService;
import service.MovieService;
import service.ReportService;
import service.RoomService;
import service.SeatService;
import service.ShowtimeService;
import service.UserManagementService;

import java.util.Scanner;

public abstract class AbstractConsoleHandler {
    protected final ConsoleContext context;
    protected final ConsolePrinter printer;

    protected AbstractConsoleHandler(ConsoleContext context, ConsolePrinter printer) {
        this.context = context;
        this.printer = printer;
    }

    protected Scanner scanner() {
        return context.getScanner();
    }

    protected AuthService authService() {
        return context.getAuthService();
    }

    protected BookingService bookingService() {
        return context.getBookingService();
    }

    protected CinemaService cinemaService() {
        return context.getCinemaService();
    }

    protected GenreService genreService() {
        return context.getGenreService();
    }

    protected MovieService movieService() {
        return context.getMovieService();
    }

    protected ReportService reportService() {
        return context.getReportService();
    }

    protected RoomService roomService() {
        return context.getRoomService();
    }

    protected SeatService seatService() {
        return context.getSeatService();
    }

    protected ShowtimeService showtimeService() {
        return context.getShowtimeService();
    }

    protected UserManagementService userManagementService() {
        return context.getUserManagementService();
    }
}
