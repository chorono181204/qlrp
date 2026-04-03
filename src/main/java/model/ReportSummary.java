package model;

import java.math.BigDecimal;

public record ReportSummary(
        int totalBookings,
        int paidBookings,
        int cancelledBookings,
        int totalTicketsSold,
        BigDecimal totalRevenue
) {
}
