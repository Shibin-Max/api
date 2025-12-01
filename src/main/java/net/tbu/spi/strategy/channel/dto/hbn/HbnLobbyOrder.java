package net.tbu.spi.strategy.channel.dto.hbn;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public final class HbnLobbyOrder {

    /**
     * PlayerId<br>
     * String<br>
     * Internal Habanero GUID for player
     */
    @JSONField(name = "PlayerId")
    private String playerId;

    /**
     * BrandId <br>
     * String<br>
     * NOTE: Brandid is populated for
     * GetGroupCompletedGameResultsV2 method
     */
    @JSONField(name = "BrandId")
    private String brandId;

    /**
     * Username<br>
     * String<br>
     * Player Username
     */
    @JSONField(name = "Username")
    private String username;

    /**
     * BrandGameId<br>
     * String<br>
     * Game BrandGame ID
     */
    @JSONField(name = "BrandGameId")
    private String brandGameId;

    /**
     * GameKeyName<br>
     * String<br>
     * Game Indentifier
     */
    @JSONField(name = "GameKeyName")
    private String gameKeyName;

    /**
     * GameTypeId<br>
     * Int<br>
     * See gametype addendum
     */
    @JSONField(name = "GameTypeId")
    private int gameTypeId;

    /**
     * DtStarted<br>
     * String<br>
     * Start date of game round
     */
    @JSONField(name = "DtStarted")
    private String dtStarted;

    /**
     * DtCompleted<br>
     * String<br>
     * Completed date of game round
     */
    @JSONField(name = "DtCompleted")
    private String dtCompleted;

    /**
     * FriendlyGameInstanceId<br>
     * Int64<br>
     * Unique Game ID as a long integer
     */
    @JSONField(name = "FriendlyGameInstanceId")
    private long friendlyGameInstanceId;

    /**
     * GameInstanceId<br>
     * String<br>
     * Unique Game ID as a GUID
     */
    @JSONField(name = "GameInstanceId")
    private String gameInstanceId;

    /**
     * GameStateId<br>
     * Int<br>
     * 3 – Completed<br>
     * 4 – Voided (Insufficient funds)<br>
     * 11 - Expired
     */
    @JSONField(name = "GameStateId")
    private int gameStateId;

    /**
     * Stake<br>
     * Decimal<br>
     * Real money stake amount
     */
    @JSONField(name = "Stake")
    private double stake;

    /**
     * Payout<br>
     * Decimal<br>
     * Real money payout amount
     */
    @JSONField(name = "Payout")
    private double payout;

    /**
     * JackpotWin<br>
     * Decimal<br>
     * Portion of the Payout which was from a Jackpot Win
     */
    @JSONField(name = "JackpotWin")
    private double jackpotWin;

    /**
     * JackpotContribution<br>
     * Decimal<br>
     * Jackpot contribution amount
     */
    @JSONField(name = "JackpotContribution")
    private double jackpotContribution;

    /**
     * CurrencyCode<br>
     * String<br>
     * Currency code of Player
     */
    @JSONField(name = "CurrencyCode")
    private String currencyCode;

    /**
     * ChannelTypeId<br>
     * Int<br>
     * See addendum of Channels
     */
    @JSONField(name = "ChannelTypeId")
    private int channelTypeId;

    /**
     * BalanceAfter<br>
     * Decimal<br>
     * Real balance after game completed
     */
    @JSONField(name = "BalanceAfter")
    private double balanceAfter;

    /**
     * BonusStake<br>
     * Decimal<br>
     * Bonus Stake amount (if the game used bonus)
     */
    @JSONField(name = "BonusStake")
    private double bonusStake;

    /**
     * BonusPayout<br>
     * Decimal<br>
     * Bonus Payout amount (if the game used bonus)
     */
    @JSONField(name = "BonusPayout")
    private double bonusPayout;

    /**
     * BonusToReal<br>
     * Decimal<br>
     * Converted amount from Bonus to Real Balance
     * (IMPORTANT FOR BONUSING!)
     * Identifies how much money was converted from Free
     * Spin Bonus to Real Balance. It is not shown elsewhere
     */
    @JSONField(name = "BonusToReal")
    private double bonusToReal;

    /**
     * BonusCoupon<br>
     * String<br>
     * Coupon Code used for the Bonus (if the game used bonus)
     */
    @JSONField(name = "BonusCoupon")
    private String bonusCoupon;

    /**
     * FeatureCount<br>
     * Int<br>
     * Number of feature spins/actions for the game.
     * A non 0 number means a feature was hit.
     */
    @JSONField(name = "FeatureCount")
    private int featureCount;

    /**
     * BuyFeatureId<br>
     * Int<br>
     * A non 0 number means a player used the "Buy Feature" option.
     * If you want a mapping of the BuyFeatureId,
     * please refer to Addendum L
     */
    @JSONField(name = "BuyFeatureId")
    private int buyFeatureId;

}