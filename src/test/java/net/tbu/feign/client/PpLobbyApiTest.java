package net.tbu.feign.client;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import net.tbu.feign.client.external.PpLobbyApi;
import org.eclipse.collections.api.list.MutableList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class PpLobbyApiTest {

    @Resource
    private PpLobbyApi ppLobbyApi;

    @Value("${pp.channel.envDomain:api.prerelease-env.biz}")
    private String envDomain;

    @Value("${pp.channel.envUri:api.prerelease-env.biz}")
    private String envUri;

    @Value("${pp.channel.dataDomain:api.prerelease-env.biz}")
    private String dataDomain;

    @Value("${pp.channel.dataUri:api.prerelease-env.biz}")
    private String dataUri;

    @Value("${pp.channel.login:bngplspr_bingopluspr}")
    private String login;

    @Value("${pp.channel.password:xXQVYcCmP2eeJa3E}")
    private String password;

    @Test
    void getEnvironments() {
        try {
            String environments = ppLobbyApi.getEnvironments(envDomain, login, password);
            System.out.println(environments);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getTransactions() {
        try {
            MutableList<String> transactions = ppLobbyApi.getTransactions(dataDomain, login, password,
                    ZonedDateTime.of(LocalDate.of(2025, 1, 10),
                                    LocalTime.MIN, ZoneId.systemDefault())
                            .toInstant().toEpochMilli());
            transactions.each(System.out::println);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getGameRounds() {
        try {
            List<String> gameRounds = ppLobbyApi.getGameRounds(dataDomain, login, password,
                    ZonedDateTime.of(LocalDate.of(2025, 1, 10),
                                    LocalTime.MIN, ZoneId.systemDefault())
                            .toInstant().toEpochMilli());
            System.out.println(gameRounds.size());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}