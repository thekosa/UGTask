package pl.kosieradzki.ugtask.service;

import pl.kosieradzki.ugtask.model.Computer;
import pl.kosieradzki.ugtask.model.PurchaseInput;
import pl.kosieradzki.ugtask.repository.ComputerRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ComputerService {

    private final NBPClient nbpClient;
    private final ComputerRepository repository;

    public ComputerService(NBPClient nbpClient, ComputerRepository repository) {
        this.nbpClient = nbpClient;
        this.repository = repository;
    }

    public List<Computer> convertAndSave(List<PurchaseInput> purchases) throws IOException, InterruptedException, SQLException {
        List<Computer> computers = new ArrayList<>();
        for (PurchaseInput purchase : purchases) {
            BigDecimal usdRate = nbpClient.getUsdRate(purchase.accountingDate());
            BigDecimal convertedPrice = purchase.costUsd().multiply(usdRate).setScale(2, RoundingMode.HALF_UP);
            Computer computer = new Computer(purchase.name(), purchase.accountingDate(), purchase.costUsd(), convertedPrice);
            computers.add(computer);
        }
        repository.saveAll(computers);
        return computers;
    }

    public List<Computer> listSorted(String nameFragment, LocalDate date, String column) throws SQLException {
        return repository.searchAndFindSorted(nameFragment, date, column);
    }

    public List<Computer> listAll() throws SQLException {
        return repository.findAll();
    }

    public boolean isDatabaseEmpty() throws SQLException {
        return repository.isTableEmpty("komputery");
    }
}
