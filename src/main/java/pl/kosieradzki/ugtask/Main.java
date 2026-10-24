package pl.kosieradzki.ugtask;

import pl.kosieradzki.ugtask.model.Computer;
import pl.kosieradzki.ugtask.model.PurchaseInput;
import pl.kosieradzki.ugtask.repository.ComputerRepository;
import pl.kosieradzki.ugtask.service.ComputerService;
import pl.kosieradzki.ugtask.service.NBPClient;
import pl.kosieradzki.ugtask.xml.XmlExporter;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class Main {

    public static void main(String[] args) throws IOException, SQLException, InterruptedException, XMLStreamException {
        ComputerRepository repository = new ComputerRepository(Path.of("data", "ugtask.db"));
        repository.initialize();

        NBPClient nbpClient = new NBPClient();
        ComputerService computerService = new ComputerService(nbpClient, repository);
        XmlExporter xmlExporter = new XmlExporter();


        if (computerService.isDatabaseEmpty()) {
            List<PurchaseInput> purchases = List.of(
                    new PurchaseInput("ACER Aspire", LocalDate.of(2026, 7, 3), new BigDecimal("345")),
                    new PurchaseInput("DELL Latitude", LocalDate.of(2026, 7, 12), new BigDecimal("543")),
                    new PurchaseInput("HP Victus", LocalDate.of(2026, 7, 15), new BigDecimal("346"))
            );
            computerService.convertAndSave(purchases);
        }
        List<Computer> computers = repository.findAll();
        xmlExporter.export(computers, Path.of("data", "Komputery.xml"));

        System.out.println(computersView(computers));

        while (true) {
            System.out.println("Podaj nazwę komputera albo datę zaksięgowania(dd.mm.rrrr) oraz po : nazwę kolumny po której ma zostać posortowana(nazwa/data); pusta komenda kończy pracę programu.");
            System.out.println("Przykładowo: \"ACER:nazwa\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            if (input.isEmpty()) {
                return;
            }

            String[] inputParts = input.split(":");
            if (inputParts.length != 2) {
                System.out.println("Niepoprawny format. Wpisz np. ACER:nazwa lub 03.07.2026:data.");
                continue;
            }

            String filter = inputParts[0].trim();
            String sortBy = inputParts[1].trim().toLowerCase(Locale.ROOT);
            if (!sortBy.matches("(?i)(nazwa|data)")) {
                System.out.println("Po dwukropku wpisz nazwa albo data.");
                continue;
            }

            LocalDate filterDate = null;
            String filterName = null;
            if (filter.matches("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}")) {
                try {
                    String[] dateParts = filter.split("\\.");
                    filterDate = LocalDate.of(
                            Integer.parseInt(dateParts[2]),
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[0]));

                } catch (DateTimeException exception) {
                    System.out.println("Taka data nie istnieje. Podaj poprawną datę w formacie dd.mm.rrrr.");
                    continue;
                }
            } else if (filter.matches("[\\p{L}0-9 ]+")) {
                filterName = filter;
            } else {
                System.out.println("Niepoprawna data lub nazwa. Podaj datę dd.mm.rrrr albo nazwę z liter, cyfr i spacji.");
                continue;
            }

            if (!sortBy.equals("nazwa") && !sortBy.equals("data")) {
                System.out.println("Niepoprawna nazwa kolumny sortowania. Podaj \"nazwa\" lub \"data\".");
                continue;
            }

            if (sortBy.equals("data")) {
                sortBy = "data_ksiegowania";
            }

            System.out.println(computersView(computerService.listSorted(filterName, filterDate, sortBy)));
        }


    }

    private static String computersView(List<Computer> computers) {
        StringBuilder sb = new StringBuilder();
        for (Computer computer : computers) {
            sb.append(computer.name()).append(" : ")
                    .append(computer.accountingDate()).append(" : ")
                    .append(computer.costUSD()).append(" USD : ")
                    .append(computer.costPLN()).append(" PLN");
            sb.append("\n");
        }
        return sb.toString();
    }
}
