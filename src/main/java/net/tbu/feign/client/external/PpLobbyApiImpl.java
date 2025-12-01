package net.tbu.feign.client.external;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;
import static java.util.Optional.ofNullable;
import static net.tbu.common.utils.encrypt.MD5Utils.md5Encrypt;

/**
 *
 */
@Slf4j
@Component
public class PpLobbyApiImpl implements PpLobbyApi {

    private final HttpClient httpClient = HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(60))
            .build();

    /**
     * GET参数: ?secureLogin=${secureLogin}&hash=${hash}
     */
    private static final String DEFAULT_ENVIRONMENTS_URI = "/IntegrationService/v3/http/SystemAPI/environments";

    /*
     * NOTE: URI最后的'/'不可以去掉, 否则请求会404
     * GET参数 ?login=bngplspr_bingopluspr&password=xXQVYcCmP2eeJa3E&startDate=2025-01-07 00:00:00&endDate=2025-01-08 00:00:00&options=addTotalPromo
     */
    // private static final String DEFAULT_TOTALS_DAILY_URI = "/IntegrationService/v3/DataFeeds/totals/daily/";

    /**
     * GET参数: ?login=${login}&password=${password}&timepoint=${timepoint}&options=addTransactionStatus
     */
    private static final String DEFAULT_TRANSACTIONS_URI = "/IntegrationService/v3/DataFeeds/transactions";

    /**
     * NOTE: URI最后的'/'不可以去掉, 否则请求会404
     * GET参数: ?login=${login}&password=${password}&timepoint=${timepoint}
     */
    private static final String DEFAULT_GAME_ROUNDS_URI = "/IntegrationService/v3/DataFeeds/gamerounds/finished/";

    private static final String HTTPS = "https://";

    @Override
    @Nonnull
    public String getEnvironments(String domain, String login, String password)
            throws IOException, InterruptedException {
        return sendGetRequest(HTTPS + domain + DEFAULT_ENVIRONMENTS_URI
                              + "?secureLogin=" + login
                              + "&hash=" + md5Encrypt("secureLogin=" + login + password));
    }

    @Override
    public MutableList<String> getTransactions(String domain, String login, String password, long timepoint)
            throws IOException, InterruptedException {
        return sendGetRequestByList(HTTPS + domain + DEFAULT_TRANSACTIONS_URI
                              + "?login=" + login
                              + "&password=" + password
                              + "&timepoint=" + timepoint
                              + "&options=addTransactionStatus");
    }

    @Override
    public MutableList<String> getGameRounds(String domain, String login, String password, long timepoint)
            throws IOException, InterruptedException {
        return sendGetRequestByList(HTTPS + domain + DEFAULT_GAME_ROUNDS_URI
                                    + "?login=" + login
                                    + "&password=" + password
                                    + "&timepoint=" + timepoint);
    }
    private String sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        log.info("[PpLobbyApi]: [action:sendGetRequest] [step:initRequest] [uri:{}]", request.uri());
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            var responseBody = ofNullable(response.body()).orElse("");
            if (responseBody.length() < 8192) {
                log.info("[PpLobbyApi]: [action:sendGetRequest] [step:responseReceived] [uri:{}] [statusCode:{}] [body:{}]",
                        request.uri(), response.statusCode(), responseBody);
            } else {
                log.info("[PpLobbyApi]: [action:sendGetRequest] [step:responseReceived] [uri:{}] [statusCode:{}] [bodySize:{}]",
                        request.uri(), response.statusCode(), responseBody.length());
            }
            return responseBody;
        } catch (InterruptedException | IOException e) {
            log.error("[PpLobbyApi]: [action:sendGetRequest] [step:error] [uri:{}] [message:{}]",
                    request.uri(), e.getMessage(), e);
            throw e;
        }
    }


    private MutableList<String> sendGetRequestByList(String url)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        log.info("[PpLobbyApi]: [action:sendGetRequestByList] [step:initRequest] [uri:{}]", request.uri());
        try {
            MutableList<String> list = FastList.newList(0x200_000);
            var response = httpClient.send(request, ofInputStream());
            try (InputStream stream = response.body()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                log.info("[PpLobbyApi]: [action:sendGetRequestByList] [step:responseReceived] [uri:{}] [statusCode:{}] [available:{}]",
                        request.uri(), response.statusCode(), stream.available());
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                log.info("[PpLobbyApi]: [action:sendGetRequestByList] [step:readComplete] [lines:{}]", list.size());
                return list;
            }
        } catch (InterruptedException | IOException e) {
            log.error("[PpLobbyApi]: [action:sendGetRequestByList] [step:error] [uri:{}] [message:{}]",
                    request.uri(), e.getMessage(), e);
            throw e;
        }
    }


}
