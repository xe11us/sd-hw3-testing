package http;

import dao.AccountDao;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static utils.HttpUtils.*;

public class RxNettyAccountHttpServer {
    private final AccountDao dao;

    public RxNettyAccountHttpServer(AccountDao dao) {
        this.dao = dao;
    }

    public <T> Observable<String> getResponse(HttpServerRequest<T> request) {
        String path = request.getDecodedPath().substring(1);
        switch(request.getDecodedPath().substring(1)) {
            case "add_user": return addUser(request);
            case "add_money": return addMoney(request);
            case "get_user_stocks_info": return getUserStocksInfo(request);
            case "buy": return buy(request);
            case "sell": return sell(request);
            case "get_balance": return getBalance(request);
        }
        return Observable.just("Unsupported request: " + path);
    }

    private <T> Observable<String> addUser(HttpServerRequest<T> request) {
        Optional<String> error = checkRequestParameters(request, Collections.singletonList("id"));
        return error
                .map(Observable::just)
                .orElseGet(() ->
                        dao.addUser(getLongParam(request, "id"))
                                .map(Objects::toString)
                                .onErrorReturn(Throwable::getMessage)
                );
    }

    private <T> Observable<String> addMoney(HttpServerRequest<T> request) {
        Optional<String> error = checkRequestParameters(request, Arrays.asList("id", "count"));
        return error
                .map(Observable::just)
                .orElseGet(() ->
                        dao.addMoney(getLongParam(request, "id"), getIntParam(request, "count"))
                                .map(Objects::toString)
                                .onErrorReturn(Throwable::getMessage)
                );
    }

    private <T> Observable<String> getUserStocksInfo(HttpServerRequest<T> request) {
        Optional<String> error = checkRequestParameters(request, Collections.singletonList("id"));
        return error
                .map(Observable::just)
                .orElseGet(() ->
                        dao.getUserStocksInfo(getLongParam(request, "id"))
                                .map(Objects::toString)
                                .reduce("", (s1, s2) -> s1 + ",\n" + s2)
                );
    }

    private <T> Observable<String> getBalance(HttpServerRequest<T> request) {
        Optional<String> error = checkRequestParameters(request, Collections.singletonList("id"));
        return error
                .map(Observable::just)
                .orElseGet(() ->
                        dao.getBalance(getLongParam(request, "id"))
                                .map(Objects::toString)
                                .onErrorReturn(Throwable::getMessage)
                );
    }

    private <T> Observable<String> buy(HttpServerRequest<T> request) {
        Optional<String> error = checkRequestParameters(request, Arrays.asList("id", "company", "count"));
        return error.map(Observable::just).orElseGet(() -> dao.buy(
                        getLongParam(request, "id"),
                        getQueryParam(request, "company"),
                        getIntParam(request, "count")
                )
                .map(Objects::toString)
                .onErrorReturn(Throwable::getMessage));
    }

    private <T> Observable<String> sell(HttpServerRequest<T> request) {
        Optional<String> error = checkRequestParameters(request, Arrays.asList("id", "company", "count"));
        return error.map(Observable::just).orElseGet(() -> dao.sell(
                        getLongParam(request, "id"),
                        getQueryParam(request, "company"),
                        getIntParam(request, "count")
                )
                .map(Objects::toString)
                .onErrorReturn(Throwable::getMessage));
    }
}
