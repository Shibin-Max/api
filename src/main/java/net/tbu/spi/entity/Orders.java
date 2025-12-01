package net.tbu.spi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.tbu.spi.util.EntityBeanUtil;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 内部注单明细表
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-25
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class Orders implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 注单号码betid
     */
    @TableId("BILLNO")
    private String billno;

    /**
     * 会员账号
     */
    @TableField("LOGINNAME")
    private String loginname;

    /**
     * 代理编号
     */
    @TableField("AGCODE")
    private String agcode;

    /**
     * 顶级代理编号
     */
    @TableField("TOP_AGCODE")
    private String topAgcode;

    /**
     * 产品id
     */
    @TableField("PRODUCT_ID")
    private String productId;

    /**
     * 平台ID
     */
    @TableField("PLATFORM_ID")
    private String platformId;

    /**
     * BetAmount(投注额)
     */
    @TableField("ACCOUNT")
    private BigDecimal account;

    /**
     * 有效投注额
     */
    @TableField("VALID_ACCOUNT")
    private BigDecimal validAccount;

    /**
     * 客户输赢额度Payoff
     */
    @TableField("CUS_ACCOUNT")
    private BigDecimal cusAccount;

    /**
     * 投注前额度（原额度）
     */
    @TableField("PREVIOS_AMOUNT")
    private BigDecimal previosAmount;

    /**
     * 局号
     */
    @TableField("GMCODE")
    private String gmcode;

    /**
     * 下注时间
     */
    @TableField("BILLTIME")
    private LocalDateTime billtime;

    /**
     * 更新时间
     */
    @TableField("RECKONTIME")
    private LocalDateTime reckontime;

    /**
     * 注单状态：1已结算，0未结算，-1重置试玩额度，-2注单被篡改,-8取消指定局注单,-9取消注单
     */
    @TableField("FLAG")
    private Integer flag;

    /**
     * 校验码
     */
    @TableField("HASHCODE")
    private String hashcode;

    @TableField("PLAYTYPE")
    private Integer playtype;

    /**
     * 币别
     */
    @TableField("CURRENCY")
    private String currency;

    /**
     * 桌号
     */
    @TableField("TABLECODE")
    private String tablecode;

    /**
     * 场次
     */
    @TableField("ROUND")
    private String round;

    /**
     * 游戏种类
     */
    @TableField("GAMETYPE")
    private String gametype;

    /**
     * 客户当前IP
     */
    @TableField("CUR_IP")
    private String curIp;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 输赢/开牌结果
     */
    @TableField("RESULT")
    private String result;

    /**
     * 纸牌列表
     */
    @TableField("CARD_LIST")
    private String cardList;

    /**
     * 汇率
     */
    @TableField("EXCHANGERATE")
    private BigDecimal exchangerate;

    /**
     * 结果类型
     */
    @TableField("RESULTTYPE")
    private String resulttype;

    /**
     * 游戏种类
     */
    @TableField("GAME_KIND")
    private Integer gameKind;

    /**
     * 注单产生时间
     */
    @TableField("ORIGNAL_BILLTIME")
    private LocalDateTime orignalBilltime;

    /**
     * 原始估算时间
     */
    @TableField("ORIGNAL_RECKONTIME")
    private LocalDateTime orignalReckontime;

    /**
     * 原来时区
     */
    @TableField("ORIGNAL_TIMEZONE")
    private String orignalTimezone;

    /**
     * 创建时间（系统自己创建的时间）
     */
    @TableField("CREATION_TIME")
    private LocalDateTime creationTime;

    /**
     * 货币类型：0 CNY，1 未知，2 BTC，3 GBP
     */
    @TableField("CURRENCY_TYPE")
    private Integer currencyType;

    @TableField("HOME_TEAM")
    private String homeTeam;

    @TableField("AWAY_TEAM")
    private String awayTeam;

    @TableField("WINNING_TEAM")
    private String winningTeam;

    /**
     * 0-电脑客户端注单 1-手机
     */
    @TableField("DEVICE_TYPE")
    private String deviceType;

    /**
     * 红利金额
     */
    @TableField("BONUS_AMOUNT")
    private BigDecimal bonusAmount;

    /**
     * 0:参与洗码 1:不参与洗码
     */
    @TableField("IS_SPECIAL_GAME")
    private Integer isSpecialGame;

    /**
     * 参与反水的金额
     */
    @TableField("REMAIN_AMOUNT")
    private BigDecimal remainAmount;

    @TableField("PRO_FLAG")
    private Integer proFlag;

    /**
     * 赔率
     */
    @TableField("ODDS")
    private BigDecimal odds;

    /**
     * 盘口
     */
    @TableField("ODDSTYPE")
    private String oddstype;

    @TableField("TERMTYPE")
    private String termtype;

    @TableField("JACKPOT_AMOUNT")
    private BigDecimal jackpotAmount;

    @TableField("BG_CARD_NUM")
    private Integer bgCardNum;

    @TableField("BG_REMARK")
    private String bgRemark;

    @TableField("FIXPOOLPERCARD")
    private BigDecimal fixpoolpercard;

    @TableField("BINGO_FIXPOOLPERCARD")
    private BigDecimal bingoFixpoolpercard;

    @TableField("BINGO_1TG_NUM")
    private Integer bingo1tgNum;

    @TableField("BINGO_2TG_NUM")
    private Integer bingo2tgNum;

    @TableField("BINGO_1TG_REWARD")
    private BigDecimal bingo1tgReward;

    @TableField("BINGO_2TG_REWARD")
    private BigDecimal bingo2tgReward;

    @TableField("BETTYPE")
    private Long bettype;

    @TableField("CALLJACKPOTBALL")
    private Long calljackpotball;

    @TableField("PRICE")
    private BigDecimal price;

    @TableField("GRAPHICTYPE")
    private Integer graphictype;

    /**
     * 线上/线下：0-线上，1线下
     */
    @TableField("IS_ONLINE")
    private Integer isOnline;

    @TableField("FIXED_TIME")
    private LocalDateTime fixedTime;

    /**
     * 游戏名称
     */
    @TableField("GAME_NAME")
    private String gameName;

    /**
     * 门店code
     */
    @TableField("BRANCH_CODE")
    private String branchCode;

    /**
     * 门店名称
     */
    @TableField("BRANCH_NAME")
    private String branchName;

    @TableField("BINGO_ID")
    private String bingoId;

    /**
     * 是否中jackpot: 0否,1是
     */
    @TableField("IS_JACKPOT")
    private Integer isJackpot;

    /**
     * bingoGGR
     */
    @TableField("BINGOGGR")
    private BigDecimal bingoggr;

    /**
     * jackpot中奖
     */
    @TableField("JACKPOT")
    private BigDecimal jackpot;

    /**
     * 旁注中奖
     */
    @TableField("WON")
    private BigDecimal won;

    /**
     * siteId 1bingoplus 2laroplus
     */
    @TableField("SITE_ID")
    private Integer siteId;

    /**
     * 新JACKPOT中奖
     */
    @TableField("JACKPOT_NEW")
    private BigDecimal jackpotNew;

    /**
     * 新输赢值
     */
    @TableField("WINLOSS_NEW")
    private BigDecimal winlossNew;

    /**
     * betSiteId 1Bingoplus 2Arenaplus
     */
    @TableField("BET_SITE_ID")
    private Integer betSiteId;

    /**
     * 设备ID
     */
    @TableField("DEVICE_ID")
    private String deviceId;

    /**
     * appsFlyerId
     */
    @TableField("APPS_FLYER_ID")
    private String appsFlyerId;

    /**
     * 业务线：AP、BP、GP、PG
     */
    @TableField("TENANT")
    private String tenant;

    /**
     * 倍数
     */
    @TableField("TIMES")
    private Integer times;

    /**
     * 订单引用
     */
    @TableField(exist = false)
    private String orderRef;

    /**
     * 返回可以索引到内部注单的注单引用
     *
     * @return String
     */
    public String getOrderRef() {
        if (orderRef == null) {
            this.orderRef = EntityBeanUtil.getOrderRef(billno, platformId);
        }
        return orderRef;
    }


}
