package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class User {
    private final long id;
    private int money;
    private final Map<String, Stocks> stocks = new HashMap<>();

    public User(long id, int money) {
        this.id = id;
        this.money = money;
    }

    public long getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int addition) {
        money += addition;
    }

    public Collection<Stocks> getStocks() {
        return stocks.values();
    }

    public void buyStocks(String company, int price, int count) {
        if (price * count > money) {
            throw new IllegalArgumentException("Not enough money");
        }
        stocks.put(company, stocks.getOrDefault(company, new Stocks(company, 0, 0)).add(count));
        money -= price * count;
    }

    public void sellStocks(String company, int price, int count) {
        if (!stocks.containsKey(company) || stocks.get(company).getAmount() < count) {
            throw new IllegalArgumentException("Not enough stocks");
        }
        stocks.put(company, stocks.get(company).minus(count));
        money += price * count;
    }
}
