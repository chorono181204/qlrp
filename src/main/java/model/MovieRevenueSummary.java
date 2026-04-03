package model;

import java.math.BigDecimal;

public record MovieRevenueSummary(
        String movieTitle,
        int ticketsSold,
        BigDecimal revenue
) {
}
