package net.tbu.spi.mapper.provider;

import lombok.extern.slf4j.Slf4j;
import net.tbu.spi.dto.OrderRequestDTO;

import javax.annotation.Nonnull;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class OrdersProvider {

    /**
     * 查询汇总
     */
    public String getOrdersSummaryByParams(OrderRequestDTO dto) {
        var tableName = getTableName(dto.getTableNameSuffix());
        var sqlBuilder = new StringBuilder(
                "SELECT " +
                "IFNULL(SUM(ACCOUNT), 0) AS sumBetAmount, " +
                "IFNULL(SUM(VALID_ACCOUNT), 0) AS sumEffBetAmount, " +
                "IFNULL(SUM(CUS_ACCOUNT), 0) AS sumWlValue, " +
                "COUNT(1) AS sumUnitQuantity " +
                " FROM " + tableName + " WHERE 1=1 ");
        fillQueryConditions(sqlBuilder, dto);
        var sql = sqlBuilder.toString();
        log.info("[OrdersProvider.getOrdersSummaryByParams] [table_name: {}] [sql: {}] [dto: {}]", tableName, sql, dto);
        return sql;
    }

    private static final String ORDERS_QUERY_FIELDS =
            "SELECT " +
            "BILLNO AS billno, " +
            "LOGINNAME AS loginname, " +
            "AGCODE AS agcode, " +
            "PLATFORM_ID AS platformId, " +
            "REMARK AS remark, " +
            "ACCOUNT AS account, " +
            "VALID_ACCOUNT AS validAccount, " +
            "CUS_ACCOUNT AS cusAccount, " +
            "BILLTIME AS billtime, " +
            "RECKONTIME AS reckontime, " +
            "CREATION_TIME AS creationTime, " +
            "FIXED_TIME AS fixedTime, " +
            "FLAG AS flag, " +
            "ROUND AS round, " +
            "GAMETYPE AS gametype, " +
            "PLAYTYPE AS playtype, " +
            "GAME_KIND AS gameKind, " +
            "BONUS_AMOUNT AS bonusAmount, " +
            "REMAIN_AMOUNT AS remainAmount";

    /**
     * 查询明细
     */
    public String getOrdersByLikeBillno(OrderRequestDTO dto) {
        var tableName = getTableName(dto.getTableNameSuffix());
        var sqlBuilder = new StringBuilder(ORDERS_QUERY_FIELDS + " FROM " + tableName + " WHERE 1 = 1 ")
                .append("AND BILLNO LIKE '%")
                .append(dto.getBillno())
                .append("' ");
        if (isNotBlank(dto.getBillTime())) {
            sqlBuilder
                    .append("AND BILLTIME >= '")
                    .append(dto.getBillTime())
                    .append(" 00:00:00' ")
                    .append("AND BILLTIME <= '")
                    .append(dto.getBillTime())
                    .append(" 23:59:59' ");
        }
        if (isNotBlank(dto.getReckonTime())) {
            sqlBuilder
                    .append("AND RECKONTIME >= '")
                    .append(dto.getReckonTime())
                    .append(" 00:00:00' ")
                    .append("AND RECKONTIME <= '")
                    .append(dto.getReckonTime())
                    .append(" 23:59:59' ");
        }
        var sql = sqlBuilder.toString();
        log.info("[OrdersProvider.getOrdersByLikeBillno] [table_name: {}] [sql: {}] [dto: {}]",
                tableName, sql, dto);
        return sql;
    }


    /**
     * 查询明细
     */
    public String getOrdersByParam(OrderRequestDTO dto) {
        var tableName = getTableName(dto.getTableNameSuffix());
        var sqlBuilder = new StringBuilder(ORDERS_QUERY_FIELDS + " from " + tableName + " where 1 = 1 ");
        fillQueryConditions(sqlBuilder, dto);
        fillPagination(sqlBuilder, dto);
        var sql = sqlBuilder.toString();
        log.info("[OrdersProvider.getOrdersByParam] [table_name: {}] [sql: {}] [dto: {}]",
                tableName, sql, dto);
        return sql;
    }


    @Nonnull
    private static String getTableName(String tableNameSuffix) {
        return "ORDERS_" + tableNameSuffix.replace("'", "");
    }

    private static void fillQueryConditions(StringBuilder sqlBuilder, OrderRequestDTO dto) {
        if (isNotBlank(dto.getBillTimeStart()) && isNotBlank(dto.getBillTimeEnd()))
            sqlBuilder.append("AND BILLTIME >= #{billTimeStart} AND BILLTIME < #{billTimeEnd} ");
        if (isNotBlank(dto.getReckonTimeStart()) && isNotBlank(dto.getReckonTimeEnd()))
            sqlBuilder.append("AND RECKONTIME >= #{reckonTimeStart} AND RECKONTIME < #{reckonTimeEnd} ");
        if (isNotBlank(dto.getSettleTimeStart()) && isNotBlank(dto.getSettleTimeEnd()))
            sqlBuilder.append("AND SETTLE_TIME >= #{settleTimeStart} AND SETTLE_TIME < #{settleTimeEnd} ");
        if (isNotBlank(dto.getRemark()))
            sqlBuilder.append("AND REMARK = #{remark} ");
        if (dto.getFlag() != null)
            sqlBuilder.append("AND FLAG = #{flag} ");
        if (isNotBlank(dto.getPlatformId()))
            sqlBuilder.append("AND PLATFORM_ID = #{platformId} ");
    }

    private static void fillPagination(StringBuilder sqlBuilder, OrderRequestDTO dto) {
        //sqlBuilder.append("ORDER BY _tidb_rowid DESC ");
        sqlBuilder.append("ORDER BY BILLNO DESC ");
        if (dto.getPageSize() != null)
            sqlBuilder.append("LIMIT #{pageSize} ");
        if (dto.getStartWith() != null)
            sqlBuilder.append("OFFSET #{startWith} ");
    }

}
