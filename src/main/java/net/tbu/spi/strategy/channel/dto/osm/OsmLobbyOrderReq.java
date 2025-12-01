package net.tbu.spi.strategy.channel.dto.osm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
@Accessors(chain = true)
public class OsmLobbyOrderReq extends LobbyBasicReq {

    /**
     * Channel ID.
     */
    private String channelId;

    /**
     * Timestamp. In milliseconds.
     */
    private String timestamp;

    /**
     * Signature.
     * <br>String splicing: username+timestamp
     * <br>Note: timestamp can be used alone if no username
     */
    private String signature;

    /**
     * Check the betting status. Default is all records
     * <br>0: query only failed records
     * <br>1: query only successful records
     */
    private String betStatus;

    /**
     * Username.
     * <br>Cannot use http reserved characters.
     */
    private String username;

    /**
     * Start of query time range.
     */
    private String startTimeStr;

    /**
     * End of query time range.
     */
    private String endTimeStr;

    /**
     * Judgment query time. Default is 0
     * <br>0: Payout time
     * <br>1: Bet time
     */
    private Integer judgeTime;

    /**
     * Page number. Default is 1.
     */
    private Integer pageNum;

    /**
     * The number of records displayed on each page. Default is 10. The maximum is 5000.
     */
    private Integer pageSize;

}