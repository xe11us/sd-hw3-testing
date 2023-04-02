package dao;

import com.mongodb.rx.client.Success;
import model.Stocks;
import rx.Observable;

public interface MarketDao {
    Observable<Success> addCompany(String name, int stocksCount, int stocksPrice);

    Observable<Stocks> getCompanies();

    Observable<Success> addStocks(String company, int stocksCount);

    Observable<Stocks> getStocksInfo(String company);

    Observable<Success> buy(String company, int count);

    Observable<Success> updatePrice(String company, int newPrice);
}
