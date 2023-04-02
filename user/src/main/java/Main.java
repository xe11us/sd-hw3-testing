import dao.AccountDaoImpl;
import http.RxNettyAccountHttpServer;
import http.MarketHttpClient;
import io.reactivex.netty.protocol.http.server.HttpServer;

public class Main {
    public static void main(String[] args) {
        RxNettyAccountHttpServer server = new RxNettyAccountHttpServer(new AccountDaoImpl(new MarketHttpClient()));
        HttpServer.newServer(8081)
                .start((req, resp) -> resp.writeString(server.getResponse(req).map(r -> r + "\n")))
                .awaitShutdown();
    }
}
