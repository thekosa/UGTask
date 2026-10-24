package pl.kosieradzki.ugtask.repository;

import pl.kosieradzki.ugtask.model.Computer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ComputerRepository {

    private final Path databaseFile;

    public ComputerRepository(Path databaseFile) {
        this.databaseFile = databaseFile.toAbsolutePath();
    }

    public void initialize() throws IOException, SQLException {
        Files.createDirectories(databaseFile.getParent());

        String createTable = """
                CREATE TABLE IF NOT EXISTS komputery (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nazwa TEXT NOT NULL,
                    data_ksiegowania TEXT NOT NULL,
                    koszt_usd DECIMAL(12,2) NOT NULL,
                    koszt_pln DECIMAL(12,2) NOT NULL
                )
                """;

        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTable);
        }
    }


    public void saveAll(List<Computer> computers) throws SQLException {
        String sql = """
                INSERT INTO komputery (nazwa, data_ksiegowania, koszt_usd, koszt_pln)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Computer computer : computers) {
                statement.setString(1, computer.name());
                statement.setString(2, computer.accountingDate().toString());
                statement.setBigDecimal(3, computer.costUSD());
                statement.setBigDecimal(4, computer.costPLN());
                statement.executeUpdate();
            }
        }
    }

    public List<Computer> findAll() throws SQLException {
        String sql = """
                SELECT * FROM komputery
                """;

        List<Computer> computers = new ArrayList<>();

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    computers.add(readComputer(resultSet));
                }
            }
        }

        return computers;
    }

    public List<Computer> searchAndFindSorted(String nameFragment, LocalDate date, String column) throws SQLException {
        String sql = """
                SELECT nazwa, data_ksiegowania, koszt_usd, koszt_pln
                FROM komputery
                WHERE nazwa LIKE ? OR data_ksiegowania = ?
                ORDER BY %s
                """.formatted(column);

        List<Computer> computers = new ArrayList<>();

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + nameFragment + "%");
            statement.setString(2, date == null ? "" : date.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    computers.add(readComputer(resultSet));
                }
            }
        }

        return computers;
    }


    public boolean isTableEmpty(String tableName) throws SQLException {
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
    }

    private Computer readComputer(ResultSet resultSet) throws SQLException {
        return new Computer(
                resultSet.getString("nazwa"),
                LocalDate.parse(resultSet.getString("data_ksiegowania")),
                resultSet.getBigDecimal("koszt_usd"),
                resultSet.getBigDecimal("koszt_pln")
        );
    }
}
