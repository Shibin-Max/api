package net.tbu.feign.client.external;

import java.io.IOException;

public interface PsLobbyApi {

    String getHourlySummary(String dc, String url, String startTime, String endTime)
            throws IOException, InterruptedException;

    String getMinutelySummary(String dc, String url, String startTime, String endTime, String group)
            throws IOException, InterruptedException;

    String getHistory(String dc, String url, String startTime, String endTime, int detailType)
            throws IOException, InterruptedException;

}
