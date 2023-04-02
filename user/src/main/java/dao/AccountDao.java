package dao;

import com.mongodb.rx.client.Success;
import model.Stocks;
import rx.Observable;

public interface AccountDao {
    Observable<Success> addUser(long uid);

    Observable<Success> addMoney(long uid, int amount);

    Observable<Stocks> getUserStocksInfo(long uid);

    Observable<Success> buy(long uid, String company, int amount);

    Observable<Success> sell(long uid, String company, int amount);

    Observable<Integer> getBalance(long uid);
}
