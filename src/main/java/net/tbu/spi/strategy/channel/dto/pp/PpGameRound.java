package net.tbu.spi.strategy.channel.dto.pp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.tbu.spi.strategy.channel.dto.LobbyOrderResult;
import net.tbu.spi.strategy.channel.dto.pp.enums.PpRoundStatusEnum;
import net.tbu.spi.strategy.channel.dto.pp.enums.PpRoundTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.StringTokenizer;

import static java.lang.Long.parseLong;
import static java.time.LocalDateTime.parse;
import static java.time.ZoneOffset.UTC;
import static net.tbu.common.constants.ComConstant.SYS_ZONE_ID;
import static net.tbu.common.utils.LocalDateTimeUtil.YYYY_MM_DD_HH_MM_SS_FMT;
import static net.tbu.spi.strategy.channel.dto.LobbyConstant.SYS_FMT;
import static net.tbu.spi.strategy.channel.dto.pp.enums.PpRoundStatusEnum.COMPLETED;
import static net.tbu.spi.strategy.channel.dto.pp.enums.PpRoundStatusEnum.FINALIZED;
import static net.tbu.spi.strategy.channel.dto.pp.enums.PpRoundTypeEnum.ROUND;

/**
 * 接口 [8.2 游戏回合] 返回数据结构
 */
@Accessors(chain = true)
@ToString
@Slf4j
public class PpGameRound {

    /**
     * 玩家在 PragmaticPlay 系统中的唯一标识符 (必需)
     */
    @Getter
    @Setter
    private long playerID;

    /**
     * 娱乐场运营商系统中唯一的玩家标识符 (必需)
     */
    @Getter
    @Setter
    private String extPlayerID;

    /**
     * 由 PragmaticPlay 提供的游戏唯一符号标识符 (必需)
     */
    @Getter
    @Setter
    private String gameID;

    /**
     * 玩家的特定游戏会话的ID (游戏回合的唯一编号) (必需)
     */
    @Getter
    @Setter
    private long playSessionID;

    /**
     * 游戏会话的ID (必需)
     * 其中特殊游戏被触发(是母游戏的唯一编号).
     * 除了免费游戏之外, 该字段将包含与所有游戏回合的playSessionID字段相同的值.
     * (免费旋转的行包含一个它在parentsessionid场和ID在playsessionid领域免费旋转被激活的游戏ID)
     */
    @Getter
    @Setter
    private long parentSessionID;

    /**
     * 游戏回合开始的日期和时间 (必需)
     * 将以 yyyy-mm-dd HH:mm:ss 格式返回, 例如: "2017-08-28 02:14:13"
     */
    @Getter
    @Setter
    private ZonedDateTime startDate;

    /**
     * 游戏回合结束的日期和时间 (必需)
     * 将以 yyyy-mm-dd HH:mm:ss 格式返回, 例如: "2017-08-28 02:14:13"
     * 如果游戏回合尚未完成, 则为空
     */
    @Getter
    @Setter
    private ZonedDateTime endDate;

    /**
     * 游戏状态 (必需)
     * I - 正在进行中(尚未完成)
     * C – 已完成
     * F – 取消或最终确定(仅适用于/gamerounds/finished/)
     */
    @Getter
    @Setter
    private PpRoundStatusEnum status;

    /**
     * 游戏类型 (必需)
     * R - 游戏回合
     * F – 免费旋转在游戏回合中触发
     */
    @Getter
    @Setter
    private PpRoundTypeEnum type;

    /**
     * 投注金额 (必需)
     */
    @Getter
    @Setter
    private BigDecimal bet;

    /**
     * 赢得金额 (必需)
     */
    @Getter
    @Setter
    private BigDecimal win;

    /**
     * 交易货币, 3个字母的ISO代码 (必需)
     */
    @Getter
    @Setter
    private String currency;

    /**
     * 累积奖金赢得的数量 (必需)
     */
    @Getter
    @Setter
    private BigDecimal jackpot;

    /**
     * 以系统时区返回的游戏回合开始的日期和时间 (自定义)
     * 将以 yyyy-mm-dd HH:mm:ss 格式返回, 例如: "2017-08-28 02:14:13"
     */
    private String adjustedStartDate;

    /**
     * @return ZonedDateTime
     */
    public String getAdjustedStartDate() {
        if (startDate == null) return "";
        if (adjustedStartDate == null)
            this.adjustedStartDate = SYS_FMT
                    .format(startDate.withZoneSameInstant(SYS_ZONE_ID).toLocalDateTime());
        return adjustedStartDate;
    }

    /**
     * 以系统时区返回的游戏回合结束的日期和时间 (自定义)
     * 将以 yyyy-mm-dd HH:mm:ss 格式返回, 例如: "2017-08-28 02:14:13"
     * 如果游戏回合尚未完成, 则为空
     */
    private String adjustedEndDate;

    /**
     * @return ZonedDateTime
     */
    public String getAdjustedEndDate() {
        if (endDate == null) return "";
        if (adjustedEndDate == null)
            this.adjustedEndDate = SYS_FMT
                    .format(endDate.withZoneSameInstant(SYS_ZONE_ID).toLocalDateTime());
        return adjustedEndDate;
    }

    private long timePoint;

    public long getTimePoint() {
        if (endDate == null) return 0L;
        if (timePoint == 0L)
            this.timePoint = endDate.toInstant().toEpochMilli();
        return timePoint;
    }

    /**
     * 解析数据样本
     * <pre>
     * timepoint=1740022596168
     * playerID,extPlayerID,gameID,playSessionID,parentSessionID,startDate,endDate,status,type,bet,win,currency,jackpot
     * 56406193,perya8by45ly,vs5aztecgems,66522376884143,null,2025-02-20 03:26:36,2025-02-20 03:26:36,C,R,0.50,7.60,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522382866143,null,2025-02-20 03:26:47,2025-02-20 03:26:47,C,R,0.50,0.00,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522384560143,null,2025-02-20 03:26:50,2025-02-20 03:26:50,C,R,0.50,0.00,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522386412143,null,2025-02-20 03:26:53,2025-02-20 03:26:53,C,R,0.50,0.00,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522386992143,null,2025-02-20 03:26:54,2025-02-20 03:26:54,C,R,0.50,0.00,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522388793143,null,2025-02-20 03:26:57,2025-02-20 03:26:58,C,R,0.50,0.00,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522391311143,null,2025-02-20 03:27:02,2025-02-20 03:27:02,C,R,0.50,0.00,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522391906143,null,2025-02-20 03:27:03,2025-02-20 03:27:03,C,R,0.50,0.00,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522393896143,null,2025-02-20 03:27:07,2025-02-20 03:27:07,C,R,0.50,12.00,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522398127143,null,2025-02-20 03:27:14,2025-02-20 03:27:15,C,R,0.50,0.00,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522400882143,null,2025-02-20 03:27:19,2025-02-20 03:27:20,C,R,0.50,0.00,PHP,0.00
     * 56406193,perya8by45ly,vs5aztecgems,66522402772143,null,2025-02-20 03:27:23,2025-02-20 03:27:23,C,R,0.50,0.00,PHP,0.00
     * </pre>
     *
     * @param data String[]
     * @return PpGameRound
     */
    public static PpGameRound newGameRoundWith(String[] data) {
        if (data != null && data.length >= 13) {
            return new PpGameRound()
                    .setPlayerID(parseLong(data[0]))
                    .setExtPlayerID(data[1])
                    .setGameID(data[2])
                    .setPlaySessionID(parseLong(data[3]))
                    .setParentSessionID(Optional.ofNullable(data[4])
                            .filter(s -> !s.isEmpty() && !s.equalsIgnoreCase("null"))
                            .map(Long::parseLong)
                            .orElse(-1L))
                    .setStartDate(ZonedDateTime.of(parse(data[5], YYYY_MM_DD_HH_MM_SS_FMT), UTC))
                    .setEndDate(ZonedDateTime.of(parse(data[6], YYYY_MM_DD_HH_MM_SS_FMT), UTC))
                    .setStatus(PpRoundStatusEnum.fromCode(data[7]))
                    .setType(PpRoundTypeEnum.fromCode(data[8]))
                    .setBet(new BigDecimal(data[9]))
                    .setWin(new BigDecimal(data[10]))
                    .setCurrency(data[11])
                    .setJackpot(new BigDecimal(data[12]));
        } else {
            log.error("PP INVALID DATA PpGameRound: {}", data == null ? "null" : Arrays.toString(data));
            return null;
        }
    }

    public static DataVerifyResult dataVerifyAndPut(String dataLine, long startTimepoint, long endTimepoint, LobbyOrderResult result) {
        if (StringUtils.isNotBlank(dataLine)) {
            var round = newGameRoundWith(dataLine.split(","));
            if (round == null) {
                return DataVerifyResult.ILLEGAL;
            }
            var roundTimepoint = round.getEndDate().toInstant().toEpochMilli();
            if ((round.getStatus() == COMPLETED || round.getStatus() == FINALIZED)
                && round.getType() == ROUND
                && roundTimepoint >= startTimepoint
                && roundTimepoint < endTimepoint) {
                result.putOrder(new PpLobbyOrderDelegate(round));
                return DataVerifyResult.VERIFIED;
            } else {
                return DataVerifyResult.UNEXPECTED;
            }
        } else {
            return DataVerifyResult.ILLEGAL;
        }
    }

    public enum DataVerifyResult {
        ILLEGAL, UNEXPECTED, VERIFIED
    }

    public static void main(String[] args) {
        String data = """
                timepoint=1740022596168
                playerID,extPlayerID,gameID,playSessionID,parentSessionID,startDate,endDate,status,type,bet,win,currency,jackpot
                56406193,perya8by45ly,vs5aztecgems,66522376884143,null,2025-02-20 03:26:36,2025-02-20 03:26:36,C,R,0.50,7.60,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522382866143,null,2025-02-20 03:26:47,2025-02-20 03:26:47,C,R,0.50,0.00,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522384560143,null,2025-02-20 03:26:50,2025-02-20 03:26:50,C,R,0.50,0.00,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522386412143,null,2025-02-20 03:26:53,2025-02-20 03:26:53,C,R,0.50,0.00,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522386992143,null,2025-02-20 03:26:54,2025-02-20 03:26:54,C,R,0.50,0.00,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522388793143,null,2025-02-20 03:26:57,2025-02-20 03:26:58,C,R,0.50,0.00,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522391311143,null,2025-02-20 03:27:02,2025-02-20 03:27:02,C,R,0.50,0.00,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522391906143,null,2025-02-20 03:27:03,2025-02-20 03:27:03,C,R,0.50,0.00,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522393896143,null,2025-02-20 03:27:07,2025-02-20 03:27:07,C,R,0.50,12.00,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522398127143,null,2025-02-20 03:27:14,2025-02-20 03:27:15,C,R,0.50,0.00,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522400882143,null,2025-02-20 03:27:19,2025-02-20 03:27:20,C,R,0.50,0.00,PHP,0.00
                56406193,perya8by45ly,vs5aztecgems,66522402772143,null,2025-02-20 03:27:23,2025-02-20 03:27:23,C,R,0.50,0.00,PHP,0.00
                """;
        StringTokenizer dataLines = new StringTokenizer(data, "\n");
        System.out.println(dataLines.countTokens());
        if (dataLines.countTokens() >= 2) {
            dataLines.nextToken();
            dataLines.nextToken();
        }
        if (dataLines.countTokens() > 0) {
            System.out.println(dataLines.countTokens());
            /// 逐行处理CSV数据
            while (dataLines.hasMoreTokens()) {
                String dataLine = dataLines.nextToken();
                System.out.println(dataLine);
            }
        } else {
            System.out.println(dataLines.countTokens());
        }

    }


}
