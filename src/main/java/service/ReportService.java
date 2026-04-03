package service;

import dao.ReportDAO;
import model.MovieRevenueSummary;
import model.ReportSummary;

import java.util.List;

public class ReportService {
    private final ReportDAO reportDAO;

    public ReportService() {
        this.reportDAO = new ReportDAO();
    }

    public ReportSummary getSummary() {
        return reportDAO.getSummary();
    }

    public List<MovieRevenueSummary> getTopMoviesByRevenue() {
        return reportDAO.getTopMoviesByRevenue();
    }
}
