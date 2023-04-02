package model;

public class Stocks {
    private final String company;
    private final int amount;
    private final int price;

    public Stocks(String company, int amount, int price) {
        this.company = company;
        this.amount = amount;
        this.price = price;
    }

    public String getCompany() {
        return company;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public Stocks updatePrice(int newPrice) {
        return new Stocks(company, amount, newPrice);
    }

    public Stocks add(int amount) {
        return new Stocks(company, this.amount + amount, price);
    }

    public Stocks minus(int amount) {
        if (this.amount < amount) {
            return this;
        }
        return new Stocks(company, this.amount - amount, price);
    }

    @Override
    public String toString() {
        return "Stocks(company = \"" + company + "\", amount = " + amount + ", price = " + price + ")";
    }
}
