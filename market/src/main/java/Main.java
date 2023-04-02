import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoDatabase;
import dao.MarketDao;
import dao.MongoMarketDao;
import io.reactivex.netty.protocol.http.server.HttpServer;
import http.RxNettyMarketHttpServer;

public class Main {
    public static void main(String[] args) {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("market");
        MarketDao dao = new MongoMarketDao(database.getCollection("companies"));

        RxNettyMarketHttpServer server = new RxNettyMarketHttpServer(dao);
        HttpServer.newServer(8080)
                .start((req, resp) ->
                        resp.writeString(server.getResponse(req).map(r -> r + "\n")))
                .awaitShutdown();
    }
}
