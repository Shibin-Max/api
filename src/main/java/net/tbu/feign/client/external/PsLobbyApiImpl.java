package net.tbu.feign.client.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


@Slf4j
@Component
public class PsLobbyApiImpl implements PsLobbyApi {

    private final HttpClient httpClient = HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public String getHourlySummary(String dc, String url, String startTime, String endTime) throws IOException, InterruptedException {
        var fullUrl = dc + url
                + "?host_id=e33de87cfa886b44a935028b8d176dd2"
                + "&start_dt=" + startTime
                + "&end_dt=" + endTime;
        return sendGetRequest(fullUrl).body();
    }

    @Override
    public String getMinutelySummary(String dc, String url, String startTime, String endTime, String group) throws IOException, InterruptedException {
        var fullUrl = dc + url
                + "?host_id=e33de87cfa886b44a935028b8d176dd2"
                + "&group_by=3"
                + "&start_dt=" + startTime
                + "&end_dt=" + endTime;
        return sendGetRequest(fullUrl).body();
    }

    @Override
    public String getHistory(String dc, String url, String startTime, String endTime, int detailType) throws IOException, InterruptedException {
        var fullUrl = dc + url
                + "?host_id=e33de87cfa886b44a935028b8d176dd2"
                + "&start_dtm=" + startTime
                + "&end_dtm=" + endTime
                + "&detail_type=" + detailType;
        return sendGetRequest(fullUrl).body();
    }


    private HttpResponse<String> sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        log.info("Sending GET request to {}", request.uri());
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
