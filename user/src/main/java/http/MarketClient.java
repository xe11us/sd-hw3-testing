package http;

public interface MarketClient {
    void buy(String company, int count);

    void sell(String company, int count);

    int getPrice(String company);

    int getAmountAvailable(String company);
}
