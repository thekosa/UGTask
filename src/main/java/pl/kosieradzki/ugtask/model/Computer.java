package pl.kosieradzki.ugtask.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Computer(
        String name,
        LocalDate accountingDate,
        BigDecimal costUSD,
        BigDecimal costPLN
) {
}
