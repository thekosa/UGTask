package pl.kosieradzki.ugtask.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseInput(String name, LocalDate accountingDate, BigDecimal costUsd) {
}
