package dao;

import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.Success;
import model.Stocks;
import org.bson.Document;
import rx.Observable;
import rx.functions.Func2;

public class MongoMarketDao implements MarketDao {
    private final MongoCollection<Document> companies;

    public MongoMarketDao(MongoCollection<Document> companies) {
        this.companies = companies;
    }

    @Override
    public Observable<Success> addCompany(String name, int stocksCount, int stocksPrice) {
        return companies
                .find(Filters.eq("company", name))
                .toObservable()
                .isEmpty()
                .flatMap(isEmpty -> {
                    if (isEmpty) {
                        return companies.insertOne(new Stocks(name, stocksCount, stocksPrice).toDocument());
                    } else {
                        return Observable.error(new IllegalArgumentException("Already exists: \"" + name + "\""));
                    }
                });
    }

    @Override
    public Observable<Stocks> getCompanies() {
        return companies.find().toObservable().map(Stocks::new);
    }

    @Override
    public Observable<Success> addStocks(String company, int stocksCount) {
        return manipulateStocks(company, Stocks::add, stocksCount);
    }

    @Override
    public Observable<Stocks> getStocksInfo(String company) {
        return companies
                .find(Filters.eq("company", company))
                .toObservable()
                .map(Stocks::new);
    }

    @Override
    public Observable<Success> buy(String company, int count) {
        return manipulateStocks(company, Stocks::minus, count);
    }

    @Override
    public Observable<Success> updatePrice(String company, int newPrice) {
        return manipulateStocks(company, Stocks::updatePrice, newPrice);
    }

    private Observable<Success> manipulateStocks(String company, Func2<Stocks, Integer, Stocks> action, int param) {
        return companies
                .find(Filters.eq("company", company))
                .toObservable()
                .map(Stocks::new)
                .defaultIfEmpty(null)
                .flatMap(stocks -> {
                    if (company == null) {
                        return Observable.error(
                                new IllegalArgumentException("Already exists: \"" + stocks.getCompany() + "\"")
                        );
                    } else {
                        return companies.replaceOne(
                                        Filters.eq("company", company),
                                        action.call(stocks, param).toDocument())
                                .map(doc -> Success.SUCCESS);
                    }
                });
    }
}
