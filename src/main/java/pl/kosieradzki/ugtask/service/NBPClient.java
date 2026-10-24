package pl.kosieradzki.ugtask.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

public class NBPClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BigDecimal getUsdRate(LocalDate date) throws IOException, InterruptedException {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Nie można pobrać kursu dla przyszłej daty.");
        }

        // W dni bez notowań szukamy ostatniego wcześniejszego kursu, do 7 dni wstecz.
        for (int daysBack = 0; daysBack <= 7; daysBack++) {
            LocalDate rateDate = date.minusDays(daysBack);
            String url = "https://api.nbp.pl/api/exchangerates/rates/a/usd/"
                    + rateDate + "/?format=json";

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                continue;
            }
            if (response.statusCode() != 200) {
                throw new IOException("Błąd API NBP: HTTP " + response.statusCode());
            }

            String rate = objectMapper.readTree(response.body()).get("rates").get(0).get("mid").asText();
            return new BigDecimal(rate);
        }

        throw new IOException("Nie znaleziono kursu USD dla " + date + " ani w ciągu 7 wcześniejszych dni.");
    }
}
