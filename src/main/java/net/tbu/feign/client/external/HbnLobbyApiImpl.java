package net.tbu.feign.client.external;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.tbu.common.utils.StringExecutors;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;
import static java.util.Optional.ofNullable;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.HBN_REQ_DT_FMT;

@Slf4j
@Component
public final class HbnLobbyApiImpl implements HbnLobbyApi {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    @Override
    public String getBrandCompletedGameResultsV2(String url, String brandId, String apiKey,
                                                 ZonedDateTime startTime, ZonedDateTime endTime)
            throws IOException, InterruptedException {
        /// 厅方使用的时间为UTC时区时间, 对时间进行时区转换
        var dtStartUTC = startTime
                .withZoneSameInstant(UTC)
                .toLocalDateTime()
                .format(HBN_REQ_DT_FMT);
        var dtEndUTC = endTime
                .withZoneSameInstant(UTC)
                .toLocalDateTime()
                .format(HBN_REQ_DT_FMT);
        var params = new HbnQueryParams().setBrandId(brandId).setApiKey(apiKey)
                .setDtStartUTC(dtStartUTC).setDtEndUTC(dtEndUTC);
        var requestBody = JSON.toJSONString(params);
        log.info("HbnLobbyApi send POST ready, body: {}", requestBody);
        var request = HttpRequest
                .newBuilder(URI.create(url))
                .POST(BodyPublishers.ofString(requestBody))
                .build();
        log.info("HbnLobbyApi sending POST request to {}, body: {}", request.uri(), requestBody);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var responseBody = ofNullable(response.body()).orElseThrow(() ->
                new IOException("HbnLobbyApi receive empty response from " + request.uri()));
        log.info("HbnLobbyApi receive POST response from {}, status code: {}, body: {}", request.uri(), response.statusCode(),
                StringExecutors.toAbbreviatedString(responseBody, 1024));
        return responseBody;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    static final class HbnQueryParams {

        @JSONField(name = "BrandId")
        private String brandId;

        @JSONField(name = "APIKey")
        private String apiKey;

        /**
         * UTC Start date for range – yyyyMMddHHmmss. This field is inclusive (>=)
         */
        @JSONField(name = "DtStartUTC")
        private String dtStartUTC;

        /**
         * UTC End date for range – yyyyMMddHHmmss. This field is exclusive (<)
         */
        @JSONField(name = "DtEndUTC")
        private String dtEndUTC;

    }

}
