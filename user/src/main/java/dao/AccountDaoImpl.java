package dao;

import com.mongodb.rx.client.Success;
import http.MarketClient;
import model.Stocks;
import model.User;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

public class AccountDaoImpl implements AccountDao {
    private final MarketClient market;
    private final Map<Long, User> users = new HashMap<>();

    public AccountDaoImpl(MarketClient market) {
        this.market = market;
    }

    @Override
    public Observable<Success> addUser(long uid) {
        if (users.containsKey(uid)) {
            return Observable.error(new IllegalArgumentException("Already registered user " + uid));
        }
        users.put(uid, new User(uid, 1000));
        return Observable.just(Success.SUCCESS);
    }

    @Override
    public Observable<Success> addMoney(long uid, int amount) {
        if (!users.containsKey(uid)) return unknownUser(uid);
        users.get(uid).addMoney(amount);
        return Observable.just(Success.SUCCESS);
    }

    @Override
    public Observable<Stocks> getUserStocksInfo(long uid) {
        if (!users.containsKey(uid)) return unknownUser(uid);
        return Observable.from(users.get(uid).getStocks()).map(this::updateStocksPrice);
    }

    @Override
    public Observable<Success> buy(long uid, String company, int amount) {
        if (!users.containsKey(uid)) return unknownUser(uid);
        try {
            if (market.getAmountAvailable(company) < amount) {
                return Observable.error(new IllegalArgumentException("Too big amount"));
            }
            users.get(uid).buyStocks(company, market.getPrice(company), amount);
            market.buy(company, amount);
            return Observable.just(Success.SUCCESS);
        } catch (IllegalArgumentException e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<Success> sell(long uid, String company, int amount) {
        if (!users.containsKey(uid)) return unknownUser(uid);
        try {
            users.get(uid).sellStocks(company, market.getPrice(company), amount);
            market.sell(company, amount);
            return Observable.just(Success.SUCCESS);
        } catch (IllegalArgumentException e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<Integer> getBalance(long uid) {
        if (!users.containsKey(uid)) return unknownUser(uid);
        User user = users.get(uid);
        return Observable.from(user.getStocks())
                .map(stocks -> updateStocksPrice(stocks).getPrice())
                .defaultIfEmpty(0)
                .reduce(Integer::sum)
                .map(value -> value + user.getMoney());
    }

    private Stocks updateStocksPrice(Stocks stocks) {
        return stocks.updatePrice(market.getPrice(stocks.getCompany()));
    }

    private<T> Observable<T> unknownUser(long uid) {
        return Observable.error(new IllegalArgumentException("Unknown user id: " + uid));
    }
}
