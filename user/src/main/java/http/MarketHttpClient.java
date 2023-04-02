package http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class MarketHttpClient implements MarketClient {
    @Override
    public void buy(String company, int count) {
        String response = sendRequest(
                "buy_stocks",
                Map.ofEntries(Map.entry("company", company), Map.entry("count", count))
        );
        if (!response.equals("SUCCESS")) {
            throw new IllegalArgumentException(response);
        }
    }

    @Override
    public void sell(String company, int count) {
        String response = sendRequest(
                "add_stocks",
                Map.ofEntries(Map.entry("company", company), Map.entry("count", count))
        );
        if (!response.equals("SUCCESS")) {
            throw new IllegalArgumentException(response);
        }
    }

    @Override
    public int getPrice(String company) {
        return parseIntOrThrow(sendRequest("get_price", Map.ofEntries(Map.entry("company", company))));
    }

    @Override
    public int getAmountAvailable(String company) {
        return parseIntOrThrow(sendRequest("get_stocks_amount", Map.ofEntries(Map.entry("company", company))));
    }

    private int parseIntOrThrow(String response) {
        try {
            return Integer.parseInt(response);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(response, e);
        }
    }

    private String sendRequest(String path, Map<String, Object> parameters) {
        String requestString = "http://localhost:8080/" + path + "?" +
                parameters.keySet().stream()
                        .map(param -> param + "=" + parameters.get(param))
                        .reduce((s1, s2) -> s1 + "&" + s2)
                        .orElse("");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(requestString))
                    .GET()
                    .build();
            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body().trim();
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
