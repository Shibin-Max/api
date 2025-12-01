package net.tbu.feign.client.external;

import org.eclipse.collections.api.list.MutableList;

import java.io.IOException;

/**
 *
 */
public interface PpLobbyApi {

    /**
     * 返回[JSON]文本
     *
     * @param domain   String
     * @param login    String
     * @param password String
     * @return String
     * @throws IOException          ioe
     * @throws InterruptedException ie
     */
    String getEnvironments(String domain, String login, String password)
            throws IOException, InterruptedException;


    /**
     * 返回[TRANSACTIONS CSV]文本
     *
     * @param domain    String
     * @param login     String
     * @param password  String
     * @param timepoint long
     * @return MutableList<String>
     * @throws IOException          ioe
     * @throws InterruptedException ie
     */
    MutableList<String> getTransactions(String domain, String login, String password, long timepoint)
            throws IOException, InterruptedException;


    /**
     * 返回[ROUND CSV]文本
     *
     * @param domain    String
     * @param login     String
     * @param password  String
     * @param timepoint long
     * @return MutableList<String>
     * @throws IOException          ioe
     * @throws InterruptedException ie
     */
    MutableList<String> getGameRounds(String domain, String login, String password, long timepoint)
            throws IOException, InterruptedException;

}
