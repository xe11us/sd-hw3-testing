import dao.AccountDao;
import dao.AccountDaoImpl;
import http.MarketHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountDaoImplTest {
    @ClassRule
    public static GenericContainer<?> marketContainer =
            new FixedHostPortGenericContainer("market:1.0-SNAPSHOT")
                    .withFixedExposedPort(8080, 8080)
                    .withExposedPorts(8080);

    private AccountDao dao;
    private final long USER_ID = 0;
    private final static String COMPANY = "company";

    @Before
    public void before() throws Exception {
        marketContainer.start();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(
                        "http://localhost:8080/add_company?name=" +
                                COMPANY +
                                "&amount=1000&price=10"
                ))
                .GET()
                .build();

        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        dao = new AccountDaoImpl(new MarketHttpClient());
        dao.addUser(USER_ID);
        dao.addMoney(USER_ID, 0);
    }

    @After
    public void tearDown() {
        marketContainer.stop();
    }

    @Test
    public void buy() {
        assertThat(dao.buy(USER_ID, COMPANY, 10).toBlocking().single())
                .asString()
                .isEqualTo("SUCCESS");
    }

    @Test
    public void sell() {
        assertThat(dao.sell(USER_ID, COMPANY, 10).toBlocking().single())
                .asString()
                .isEqualTo("SUCCESS");
    }

    @Test
    public void nonExistingCompany() {
        assertThat(dao.buy(USER_ID, "new company", 10).toBlocking().single())
                .asString()
                .isEqualTo("Already exists: \"new company\"");
    }

    @Test
    public void notEnoughStocks() {
        assertThat(dao.buy(USER_ID, COMPANY, 2000).toBlocking().single())
                .asString()
                .isEqualTo("Too big amount");
    }

    @Test
    public void notEnoughMoney() {
        assertThat(dao.buy(USER_ID, COMPANY, 150).toBlocking().single())
                .asString()
                .isEqualTo("Not enough money");
    }

    @Test
    public void unknownUserId() {
        assertThat(dao.buy(1, COMPANY, 10).toBlocking().single())
                .asString()
                .isEqualTo("Unknown user id: 1");
    }
}