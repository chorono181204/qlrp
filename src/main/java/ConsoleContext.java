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
import utils.AppConfig;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ConsoleContext {
    private final Scanner scanner;
    private final String appTitle;
    private final AuthService authService;
    private final BookingService bookingService;
    private final CinemaService cinemaService;
    private final GenreService genreService;
    private final MovieService movieService;
    private final ReportService reportService;
    private final RoomService roomService;
    private final SeatService seatService;
    private final ShowtimeService showtimeService;
    private final UserManagementService userManagementService;

    public ConsoleContext() {
        Charset appCharset = Charset.forName(AppConfig.getOrDefault("app.charset", "UTF-8"));
        this.scanner = new Scanner(new InputStreamReader(System.in, appCharset));
        this.appTitle = AppConfig.getOrDefault("app.title", "He thong quan ly rap phim");
        this.authService = new AuthService();
        this.bookingService = new BookingService();
        this.cinemaService = new CinemaService();
        this.genreService = new GenreService();
        this.movieService = new MovieService();
        this.reportService = new ReportService();
        this.roomService = new RoomService();
        this.seatService = new SeatService();
        this.showtimeService = new ShowtimeService();
        this.userManagementService = new UserManagementService();
    }

    public Scanner getScanner() {
        return scanner;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public BookingService getBookingService() {
        return bookingService;
    }

    public CinemaService getCinemaService() {
        return cinemaService;
    }

    public GenreService getGenreService() {
        return genreService;
    }

    public MovieService getMovieService() {
        return movieService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public RoomService getRoomService() {
        return roomService;
    }

    public SeatService getSeatService() {
        return seatService;
    }

    public ShowtimeService getShowtimeService() {
        return showtimeService;
    }

    public UserManagementService getUserManagementService() {
        return userManagementService;
    }
}
