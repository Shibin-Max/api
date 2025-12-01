package net.tbu.spi.mapper.provider;

import cn.hutool.json.JSONUtil;
import net.tbu.spi.dto.OrderUpdateRequestDTO;

@Deprecated
public class OrdersUpdateProvider {


    public String updateOrderByBillNo(OrderUpdateRequestDTO requestDTO) {
        String table = requestDTO.getTableNameSuffix().replace("'", "");
        StringBuilder sql = new StringBuilder(" UPDATE ORDERS_" + table + " SET ");

        boolean checkFlag = false;
        if (requestDTO.getAccount() != null) {
            sql.append(" ACCOUNT = #{account}, ");
            checkFlag = true;
        }
        if (requestDTO.getValidAccount() != null) {
            sql.append(" VALID_ACCOUNT = #{validAccount}, ");
            checkFlag = true;
        }
        if (requestDTO.getCusAccount() != null) {
            sql.append(" CUS_ACCOUNT = #{cusAccount}, ");
            checkFlag = true;
        }
        if (requestDTO.getLoginname() != null) {
            sql.append(" LOGINNAME = #{loginname}, ");
            checkFlag = true;
        }
        if (requestDTO.getAgcode() != null) {
            sql.append(" AGCODE = #{agcode}, ");
            checkFlag = true;
        }
        if (requestDTO.getTopAgcode() != null) {
            sql.append(" TOP_AGCODE = #{topAgcode}, ");
            checkFlag = true;
        }
        if (requestDTO.getProductId() != null) {
            sql.append(" PRODUCT_ID = #{productId}, ");
            checkFlag = true;
        }
        if (requestDTO.getPreviosAmount() != null) {
            sql.append(" PREVIOS_AMOUNT = #{previosAmount}, ");
            checkFlag = true;
        }
        if (requestDTO.getGmcode() != null) {
            sql.append(" GMCODE = #{gmcode}, ");
            checkFlag = true;
        }
        if (requestDTO.getBilltime() != null) {
            sql.append(" BILLTIME = #{billtime}, ");
            checkFlag = true;
        }
        if (requestDTO.getReckontime() != null) {
            sql.append(" RECKONTIME = #{reckontime}, ");
            checkFlag = true;
        }
        if (requestDTO.getFlag() != null) {
            sql.append(" FLAG = #{flag}, ");
            checkFlag = true;
        }
        if (requestDTO.getHashcode() != null) {
            sql.append(" HASHCODE = #{hashcode}, ");
            checkFlag = true;
        }
        if (requestDTO.getPlaytype() != null) {
            sql.append(" PLAYTYPE = #{playtype}, ");
            checkFlag = true;
        }
        if (requestDTO.getCurrency() != null) {
            sql.append(" CURRENCY = #{currency}, ");
            checkFlag = true;
        }
        if (requestDTO.getTablecode() != null) {
            sql.append(" TABLECODE = #{tablecode}, ");
            checkFlag = true;
        }
        if (requestDTO.getRound() != null) {
            sql.append(" ROUND = #{round}, ");
            checkFlag = true;
        }
        if (requestDTO.getGametype() != null) {
            sql.append(" GAMETYPE = #{gametype}, ");
            checkFlag = true;
        }
        if (requestDTO.getCurIp() != null) {
            sql.append(" CUR_IP = #{curIp}, ");
            checkFlag = true;
        }
        if (requestDTO.getRemark() != null) {
            sql.append(" REMARK = #{remark}, ");
            checkFlag = true;
        }
        if (requestDTO.getResult() != null) {
            sql.append(" RESULT = #{result}, ");
            checkFlag = true;
        }
        if (requestDTO.getCardList() != null) {
            sql.append(" CARD_LIST = #{cardList}, ");
            checkFlag = true;
        }
        if (requestDTO.getExchangerate() != null) {
            sql.append(" EXCHANGERATE = #{exchangerate}, ");
            checkFlag = true;
        }
        if (requestDTO.getResulttype() != null) {
            sql.append(" RESULTTYPE = #{resulttype}, ");
            checkFlag = true;
        }
        if (requestDTO.getGameKind() != null) {
            sql.append(" GAME_KIND = #{gameKind}, ");
            checkFlag = true;
        }
        if (requestDTO.getOrignalBilltime() != null) {
            sql.append(" ORIGNAL_BILLTIME = #{orignalBilltime}, ");
            checkFlag = true;
        }
        if (requestDTO.getOrignalReckontime() != null) {
            sql.append(" ORIGNAL_RECKONTIME = #{orignalReckontime}, ");
            checkFlag = true;
        }
        if (requestDTO.getOrignalTimezone() != null) {
            sql.append(" ORIGNAL_TIMEZONE = #{orignalTimezone}, ");
            checkFlag = true;
        }
        if (requestDTO.getCreationTime() != null) {
            sql.append(" CREATION_TIME = #{creationTime}, ");
            checkFlag = true;
        }
        if (requestDTO.getCurrencyType() != null) {
            sql.append(" CURRENCY_TYPE = #{currencyType}, ");
            checkFlag = true;
        }
        if (requestDTO.getHomeTeam() != null) {
            sql.append(" HOME_TEAM = #{homeTeam}, ");
            checkFlag = true;
        }
        if (requestDTO.getAwayTeam() != null) {
            sql.append(" AWAY_TEAM = #{awayTeam}, ");
            checkFlag = true;
        }
        if (requestDTO.getWinningTeam() != null) {
            sql.append(" WINNING_TEAM = #{winningTeam}, ");
            checkFlag = true;
        }
        if (requestDTO.getDeviceType() != null) {
            sql.append(" DEVICE_TYPE = #{deviceType}, ");
            checkFlag = true;
        }
        if (requestDTO.getBonusAmount() != null) {
            sql.append(" BONUS_AMOUNT = #{bonusAmount}, ");
            checkFlag = true;
        }
        if (requestDTO.getIsSpecialGame() != null) {
            sql.append(" IS_SPECIAL_GAME = #{isSpecialGame}, ");
            checkFlag = true;
        }
        if (requestDTO.getRemainAmount() != null) {
            sql.append(" REMAIN_AMOUNT = #{remainAmount}, ");
            checkFlag = true;
        }
        if (requestDTO.getProFlag() != null) {
            sql.append(" PRO_FLAG = #{proFlag}, ");
            checkFlag = true;
        }
        if (requestDTO.getOdds() != null) {
            sql.append(" ODDS = #{odds}, ");
            checkFlag = true;
        }
        if (requestDTO.getOddstype() != null) {
            sql.append(" ODDSTYPE = #{oddstype}, ");
            checkFlag = true;
        }
        if (requestDTO.getTermtype() != null) {
            sql.append(" TERMTYPE = #{termtype}, ");
            checkFlag = true;
        }
        if (requestDTO.getJackpotAmount() != null) {
            sql.append(" JACKPOT_AMOUNT = #{jackpotAmount}, ");
            checkFlag = true;
        }
        if (requestDTO.getBgCardNum() != null) {
            sql.append(" BG_CARD_NUM = #{bgCardNum}, ");
            checkFlag = true;
        }
        if (requestDTO.getBgRemark() != null) {
            sql.append(" BG_REMARK = #{bgRemark}, ");
            checkFlag = true;
        }
        if (requestDTO.getFixpoolpercard() != null) {
            sql.append(" FIXPOOLPERCARD = #{fixpoolpercard}, ");
            checkFlag = true;
        }
        if (requestDTO.getBingoFixpoolpercard() != null) {
            sql.append(" BINGO_FIXPOOLPERCARD = #{bingoFixpoolpercard}, ");
            checkFlag = true;
        }
        if (requestDTO.getBingo1tgNum() != null) {
            sql.append(" BINGO_1TG_NUM = #{bingo1tgNum}, ");
            checkFlag = true;
        }
        if (requestDTO.getBingo2tgNum() != null) {
            sql.append(" BINGO_2TG_NUM = #{bingo2tgNum}, ");
            checkFlag = true;
        }
        if (requestDTO.getBingo1tgReward() != null) {
            sql.append(" BINGO_1TG_REWARD = #{bingo1tgReward}, ");
            checkFlag = true;
        }
        if (requestDTO.getBingo2tgReward() != null) {
            sql.append(" BINGO_2TG_REWARD = #{bingo2tgReward}, ");
            checkFlag = true;
        }
        if (requestDTO.getBettype() != null) {
            sql.append(" BETTYPE = #{bettype}, ");
            checkFlag = true;
        }
        if (requestDTO.getCalljackpotball() != null) {
            sql.append(" CALLJACKPOTBALL = #{calljackpotball}, ");
            checkFlag = true;
        }
        if (requestDTO.getPrice() != null) {
            sql.append(" PRICE = #{price}, ");
            checkFlag = true;
        }
        if (requestDTO.getGraphictype() != null) {
            sql.append(" GRAPHICTYPE = #{graphictype}, ");
            checkFlag = true;
        }
        if (requestDTO.getIsOnline() != null) {
            sql.append(" IS_ONLINE = #{isOnline}, ");
            checkFlag = true;
        }
        if (requestDTO.getFixedTime() != null) {
            sql.append(" FIXED_TIME = #{fixedTime}, ");
            checkFlag = true;
        }
        if (requestDTO.getGameName() != null) {
            sql.append(" GAME_NAME = #{gameName}, ");
            checkFlag = true;
        }
        if (requestDTO.getBranchCode() != null) {
            sql.append(" BRANCH_CODE = #{branchCode}, ");
            checkFlag = true;
        }
        if (requestDTO.getBranchName() != null) {
            sql.append(" BRANCH_NAME = #{branchName}, ");
            checkFlag = true;
        }
        if (requestDTO.getBingoId() != null) {
            sql.append(" BINGO_ID = #{bingoId}, ");
            checkFlag = true;
        }
        if (requestDTO.getIsJackpot() != null) {
            sql.append(" IS_JACKPOT = #{isJackpot}, ");
            checkFlag = true;
        }
        if (requestDTO.getBingoggr() != null) {
            sql.append(" BINGOGGR = #{bingoggr}, ");
            checkFlag = true;
        }
        if (requestDTO.getJackpot() != null) {
            sql.append(" JACKPOT = #{jackpot}, ");
            checkFlag = true;
        }
        if (requestDTO.getWon() != null) {
            sql.append(" WON = #{won}, ");
            checkFlag = true;
        }
        if (requestDTO.getSiteId() != null) {
            sql.append(" SITE_ID = #{siteId}, ");
            checkFlag = true;
        }
        if (requestDTO.getJackpotNew() != null) {
            sql.append(" JACKPOT_NEW = #{jackpotNew}, ");
            checkFlag = true;
        }
        if (requestDTO.getWinlossNew() != null) {
            sql.append(" WINLOSS_NEW = #{winlossNew}, ");
            checkFlag = true;
        }
        if (requestDTO.getBetSiteId() != null) {
            sql.append(" BET_SITE_ID = #{betSiteId}, ");
            checkFlag = true;
        }
        if (requestDTO.getDeviceId() != null) {
            sql.append(" DEVICE_ID = #{deviceId}, ");
            checkFlag = true;
        }
        if (requestDTO.getAppsFlyerId() != null) {
            sql.append(" APPS_FLYER_ID = #{appsFlyerId}, ");
            checkFlag = true;
        }
        if (requestDTO.getTenant() != null) {
            sql.append(" TENANT = #{tenant}, ");
            checkFlag = true;
        }
        if (requestDTO.getTimes() != null) {
            sql.append(" TIMES = #{times}, ");
            checkFlag = true;
        }

        if (!checkFlag) {
            throw new RuntimeException("请求参数不合法:" + JSONUtil.toJsonStr(requestDTO));
        }

        sql = removeTrailingComma(sql);

        sql.append(" where BILLNO = #{billno} ");

        return sql.toString();
    }

    private StringBuilder removeTrailingComma(StringBuilder sql) {
        String trimSql = sql.toString().trim();
        if (trimSql.endsWith(",")) {
            StringBuilder newStr = new StringBuilder(trimSql);
            return newStr.replace(newStr.length() - 1, newStr.length(), "");
        }
        return sql;
    }


}

