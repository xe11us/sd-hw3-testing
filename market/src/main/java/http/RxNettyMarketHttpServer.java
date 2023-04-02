package http;

import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import dao.MarketDao;
import model.Stocks;
import utils.HttpRequestUtils;
import rx.Observable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static utils.HttpRequestUtils.getIntParam;
import static utils.HttpRequestUtils.getQueryParam;

public class RxNettyMarketHttpServer {
    private final MarketDao dao;

    public RxNettyMarketHttpServer(MarketDao dao) {
        this.dao = dao;
    }

    public <T> Observable<String> getResponse(HttpServerRequest<T> request) {
        String path = request.getDecodedPath().substring(1);
        switch (path) {
            case "add_company": return addCompany(request);
            case "get_companies": return getCompanies(request);
            case "add_stocks": return addStocks(request);
            case "get_price": return getPrice(request);
            case "get_stocks_amount": return getAmount(request);
            case "buy": return buy(request);
            case "change_price": return updatePrice(request);
        }
        return Observable.just("Unsupported request: " + path);
    }

    private <T> Observable<String> addCompany(HttpServerRequest<T> request) {
        Optional<String> error =
                HttpRequestUtils.checkRequestParameters(request, Arrays.asList("name", "amount", "price"));
        return error.map(Observable::just)
                .orElseGet(() -> dao.addCompany(
                                getQueryParam(request, "name"),
                                getIntParam(request, "amount"),
                                getIntParam(request, "price")
                        )
                        .map(Objects::toString)
                        .onErrorReturn(Throwable::getMessage));
    }

    private <T> Observable<String> getCompanies(HttpServerRequest<T> request) {
        return dao.getCompanies().map(Objects::toString).reduce("", (c1, c2) -> c1 + ",\n" + c2);
    }

    private <T> Observable<String> addStocks(HttpServerRequest<T> request) {
        Optional<String> error = HttpRequestUtils.checkRequestParameters(request, Arrays.asList("company", "count"));
        return error.map(Observable::just).orElseGet(() -> dao.addStocks(
                getQueryParam(request, "company"),
                getIntParam(request, "count")
        ).map(Objects::toString).onErrorReturn(Throwable::getMessage));
    }

    private <T> Observable<String> buy(HttpServerRequest<T> request) {
        Optional<String> error = HttpRequestUtils.checkRequestParameters(request, Arrays.asList("company", "count"));
        return error.map(Observable::just).orElseGet(() -> dao.buy(
                getQueryParam(request, "company"),
                getIntParam(request, "count")
        ).map(Objects::toString).onErrorReturn(Throwable::getMessage));
    }

    private <T> Observable<String> getPrice(HttpServerRequest<T> request) {
        Optional<String> error = HttpRequestUtils.checkRequestParameters(request, Collections.singletonList("company"));
        return error
                .map(Observable::just)
                .orElseGet(() ->
                        dao.getStocksInfo(getQueryParam(request, "company"))
                                .map(Stocks::getPrice)
                                .map(Objects::toString)
                                .onErrorReturn(Throwable::getMessage)
                );
    }

    private <T> Observable<String> getAmount(HttpServerRequest<T> request) {
        Optional<String> error = HttpRequestUtils.checkRequestParameters(request, Collections.singletonList("company"));
        return error
                .map(Observable::just)
                .orElseGet(() ->
                        dao.getStocksInfo(getQueryParam(request, "company"))
                                .map(Stocks::getAmount)
                                .map(Objects::toString)
                                .onErrorReturn(Throwable::getMessage)
                );
    }

    private <T> Observable<String> updatePrice(HttpServerRequest<T> request) {
        Optional<String> error = HttpRequestUtils.checkRequestParameters(request, Arrays.asList("company", "new_price"));
        return error
                .map(Observable::just)
                .orElseGet(() ->
                        dao.updatePrice(
                                getQueryParam(request, "company"),
                                getIntParam(request, "new_price")
                        ).map(Objects::toString).onErrorReturn(Throwable::getMessage)
                );
    }
}
