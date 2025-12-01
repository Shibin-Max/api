package net.tbu.feign.client.external;

import java.io.IOException;
import java.time.ZonedDateTime;

public interface HbnLobbyApi {

    /**
     * 返回JSON文本
     *
     * @param startTime startTime
     * @param endTime   endTime
     * @return String
     */
    String getBrandCompletedGameResultsV2(String url, String brandId, String apiKey,
                                          ZonedDateTime startTime, ZonedDateTime endTime) throws IOException, InterruptedException;

}
