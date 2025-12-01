package net.tbu.spi.strategy.channel.dto.cq9;

import com.alibaba.fastjson.JSON;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.AbstractLobbyOrder;
import net.tbu.spi.strategy.channel.dto.cq9.Cq9LobbyOrderResp.Cq9LobbyOrder;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static java.math.BigDecimal.ZERO;
import static java.util.Optional.ofNullable;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.CQ9_RSP_DT_FMT;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_ZONE_ID;


public final class Cq9LobbyOrderDelegate extends AbstractLobbyOrder<Cq9LobbyOrder> {

    public Cq9LobbyOrderDelegate(Cq9LobbyOrder delegate) {
        super(delegate);
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.CQ9;
    }

    @Nonnull
    @Override
    public String getOrderId() {
        return delegate.getRound();
    }

    @Nonnull
    @Override
    public BigDecimal getBetAmount() {
        return delegate.getBet();
    }

    @Override
    public BigDecimal getEffBetAmount() {
        if (delegate.getValidBet() != null
                && !delegate.getValidBet().equals(ZERO))
            return delegate.getValidBet();
        return getBetAmount();
    }

    private BigDecimal wlAmount;

    /**
     * 玩家損益金額(從玩家角度來計算玩家損益)：
     * 真人視訊/牌桌類：win-rake-roomfee
     * 老虎機/街機/魚機/彩票：win-bet
     *
     * @return BigDecimal
     */
    @Nonnull
    @Override
    public BigDecimal getWlAmount() {
        if (wlAmount == null)
            this.wlAmount = ofNullable(delegate.getWin()).orElse(ZERO)
                    .subtract(ofNullable(delegate.getRake()).orElse(ZERO))
                    .subtract(ofNullable(delegate.getRoomFee()).orElse(ZERO))
                    .subtract(ofNullable(delegate.getBet()).orElse(ZERO));
        return wlAmount;
    }

    private LocalDateTime betTime;

    @Nullable
    @Override
    public LocalDateTime getBetTime() {
        if (betTime == null)
            this.betTime = ZonedDateTime.parse(delegate.getBetTime(), CQ9_RSP_DT_FMT)
                    .withZoneSameInstant(SYS_ZONE_ID)
                    .toLocalDateTime();
        return betTime;
    }

    private LocalDateTime settledTime;

    @Nullable
    @Override
    public LocalDateTime getSettledTime() {
        if (settledTime == null)
            this.settledTime = ZonedDateTime.parse(delegate.getCreateTime(), CQ9_RSP_DT_FMT)
                    .withZoneSameInstant(SYS_ZONE_ID)
                    .toLocalDateTime();
        return settledTime;
    }


    public static void main(String[] args) {
        String json = """
                {
                    "data": {
                        "TotalSize": 2,
                        "Data": [
                            {
                                "gamehall": "...",
                                "gametype": "slot",
                                "gameplat": "web",
                                "gamecode": "...",
                                "account": "test001",
                                "round": "3460290676",
                                "balance": 425495.75,
                                "win": 108898.15,
                                "bet": 11,
                                "validbet": 0,
                                "jackpot": 108888,
                                "jackpotcontribution": [
                                    0.287099,
                                    0.1023,
                                    0.039599,
                                    0.0088
                                ],
                                "jackpottype": "Grand",
                                "status": "complete",
                                "endroundtime": "2020-07-14T03:40:44.162-04:00",
                                "createtime": "2020-07-14T03:40:43-04:00",
                                "bettime": "2020-07-14T03:40:42-04:00",
                                "detail": [
                                    {
                                        "freegame": 10
                                    },
                                    {
                                        "luckydraw": 0
                                    },
                                    {
                                        "bonus": 0
                                    }
                                ],
                                "singlerowbet": false,
                                "gamerole": "",
                                "bankertype": "",
                                "rake": 0,
                                "roomfee": 0,
                                "tabletype": "",
                                "tableid": "",
                                "roundnumber": "",
                                "bettype": [],
                                "gameresult": {
                                    "points": [],
                                    "cards": []
                                },
                                "ticketid": "200132",
                                "tickettype": "1",
                                "giventype": "1",
                                "ticketbets": 250,
                                "currency": "CNY",
                                "cardwin": 10.15,
                                "donate": 0,
                                "isdonate": false
                            },
                            {
                                "gamehall": "...",
                                "gametype": "live",
                                "gameplat": "web",
                                "gamecode": "...",
                                "account": "test001",
                                "round": "BV20090800000020783",
                                "balance": 184384.6,
                                "win": -30000,
                                "bet": 30000,
                                "validbet": 30000,
                                "jackpot": 0,
                                "jackpotcontribution": [],
                                "jackpottype": "",
                                "status": "complete",
                                "endroundtime": "2020-09-08T04:48:09.658-04:00",
                                "createtime": "2020-09-08T04:48:09.544-04:00",
                                "bettime": "2020-09-08T04:47:43-04:00",
                                "detail": [],
                                "singlerowbet": false,
                                "gamerole": "player",
                                "bankertype": "pc",
                                "rake": 0,
                                "roomfee": 0,
                                "currency": "CNY",
                                "tabletype": "1",
                                "tableid": "872",
                                "roundnumber": "CBG0908205199",
                                "ticketid": "",
                                "tickettype": "",
                                "giventype": "",
                                "bettype": [
                                    5,
                                    6,
                                    11
                                ],
                                "gameresult": {
                                    "points": [
                                        2,
                                        5
                                    ],
                                    "cards": [
                                        {
                                            "poker": "C3",
                                            "tag": 2
                                        },
                                        {
                                            "poker": "D10",
                                            "tag": 1
                                        },
                                        {
                                            "poker": "C11",
                                            "tag": 2
                                        },
                                        {
                                            "poker": "S2",
                                            "tag": 1
                                        },
                                        {
                                            "poker": "C2",
                                            "tag": 2
                                        },
                                        {
                                            "poker": "H10",
                                            "tag": 1
                                        }
                                    ]
                                },
                                "currency": "CNY",
                                "donate": 0,
                                "isdonate": false
                            }
                        ]
                    },
                    "status": {
                        "code": "0",
                        "message": "Success",
                        "datetime": "2019-03-04T05:42:38-04:00",
                        "traceCode": "4ObWXOk5Jek9l"
                    }
                }
                """;

        Cq9LobbyOrderResp resp = JSON.parseObject(json, Cq9LobbyOrderResp.class);

        Cq9LobbyOrderResp.Cq9LobbyStatus status = resp.getStatus();
        System.out.println(status);

        Cq9LobbyOrderResp.Cq9LobbyData data = resp.getData();

        System.out.println(data.getTotalSize());

        for (Cq9LobbyOrder order : data.getOrders()) {
            System.out.println(ZonedDateTime.parse(order.getCreateTime(), CQ9_RSP_DT_FMT));
        }

        data.getOrders().stream().map(Cq9LobbyOrderDelegate::new).forEach(System.out::println);

    }

}
