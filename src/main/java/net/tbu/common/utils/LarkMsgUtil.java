package net.tbu.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.nacos.common.codec.Base64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.tbu.config.SiteProperties;
import net.tbu.spi.entity.TReconciliationBatch;
import net.tbu.spi.entity.TReconciliationBatchRuleRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.math.RoundingMode.HALF_DOWN;
import static java.util.Optional.ofNullable;
import static net.tbu.common.enums.BatchStatusEnum.getDescByEventId;
import static net.tbu.common.enums.ReconciliationDateTypeEnum.getEnumBy;

/**
 * @author : Junjun.Ji
 * @date : 2025/2/13 11:48
 * @description :
 */
@Component
@Slf4j
public class LarkMsgUtil {

    /**
     * LarkAlertBot 回调地址
     */
    @Value("${lark.alert.bot.webhook.url}")
    private String larkAlertBotWebhookUrl;

    /**
     * LarkAlertBot 签名
     */
    @Value("${lark.alert.bot.signature}")
    private String larkAlertBotSignature;

    /**
     * 站点相关信息
     */
    @Resource
    private SiteProperties siteProperties;

    @Resource
    private ObjectMapper objectMapper;

    public static final String ELEMENT_FIELDS_IS_SHORT = "is_short";
    public static final String ELEMENT_FIELDS_TAG_LARK_MD = "lark_md";
    public static final String ELEMENT_FIELDS_TAG_MARKDOWN = "markdown";
    public static final String ELEMENT_FIELDS_CONTENT = "content";
    public static final String ELEMENT_FIELDS_TEXT = "text";

    public static final String ELEMENT_TAG_DIV = "div";
    public static final String ELEMENT_TAG_HR = "hr";
    //告警@所有人
    public static final String ELEMENT_TEXT_CONTENT_AT_ALL = "<at id=all></at>";

    /**
     * lark告警富文本消息*
     */
    private static final String LARK_ALERT_MSG_TYPE_POST = "post";

    public void sendLarkAlert(boolean successful,
                              TReconciliationBatch batch,
                              TReconciliationBatchRuleRecord ruleRecord,
                              Throwable throwable) throws JsonProcessingException {
        if (successful) {
            // 输出成功日志
            log.info(buildSuccessLog(batch));
            // 如果未对平, 发 Lark 告警
            if (isUnbalanced(batch)) {
                String contents = buildFailureContents(batch, ruleRecord, null);
                log.warn("[Reconciliation Unbalanced] {}", contents);
                sendLarkAlert(contents);
            }
            return;
        }

        // 对账失败, 发 Lark 告警
        String contents = buildFailureContents(batch, ruleRecord, throwable);
        log.error("[Reconciliation Failed] {}", contents);
        sendLarkAlert(contents);
    }

    /**
     * 判断是否未对平：状态码 3
     */
    private boolean isUnbalanced(TReconciliationBatch batch) {
        return Integer.valueOf(3).equals(batch.getBatchStatus())
               || Integer.valueOf(3).equals(batch.getReviewBatchStatus());
    }


    /**
     * 构建成功日志内容
     */
    private String buildSuccessLog(TReconciliationBatch batch) {
        String statusDesc = getDescByEventId(batch.getBatchStatus());
        String color = "已对平".equals(statusDesc) ? "green" : "red"; // 对平绿色，未对平红色
        return "<font color=\"green\">对账成功</font> " +
               "站点代码: " + siteProperties.getCode() +
               ", 厅号: " + batch.getChannelId() +
               ", 厅名: " + batch.getChannelName() +
               ", 对账日期: " + batch.getBatchDate() +
               ", 批次号: " + batch.getBatchNumber() +
               ", 最终状态: " + "<font color=\"" + color + "\">" +
               statusDesc + "</font>" +
               "\n============== 汇总数据 ==============\n" +
               "注单数(内部): " + batch.getInBetQuantity() +
               ", 注单数(外部): " + batch.getOutBetQuantity() +
               ", 投注金额(内部): " + batch.getInBetAmount().setScale(2, HALF_DOWN) +
               ", 投注金额(外部): " + batch.getOutBetAmount().setScale(2, HALF_DOWN) +
               ", 有效投注金额(内部): " + batch.getInEffBetAmount().setScale(2, HALF_DOWN) +
               ", 有效投注金额(外部): " + batch.getOutEffBetAmount().setScale(2, HALF_DOWN) +
               ", 输赢值(内部): " + batch.getInWlValue().setScale(2, HALF_DOWN) +
               ", 输赢值(外部): " + batch.getOutWlValue().setScale(2, HALF_DOWN) +
               "\n============== 平账数据 ==============\n" +
               "平账注单数: " + batch.getReconBillUnitQuantity() +
               ", 平账投注金额: " + batch.getReconBetAmount().setScale(2, HALF_DOWN) +
               ", 平账有效投注金额: " + batch.getReconEffBetAmount().setScale(2, HALF_DOWN) +
               ", 平账输赢值: " + batch.getReconWlValue().setScale(2, HALF_DOWN) +
               "\n============== 长款数据 ==============\n" +
               "长款注单数: " + batch.getLongBillUnitQuantity() +
               ", 长款投注金额: " + batch.getLongBillBetAmount().setScale(2, HALF_DOWN) +
               ", 长款有效投注金额: " + batch.getLongBillEffBetAmount().setScale(2, HALF_DOWN) +
               ", 长款输赢值: " + batch.getLongBillWlValue().setScale(2, HALF_DOWN) +
               "\n============== 短款数据 ==============\n" +
               "短款注单数: " + batch.getShortBillUnitQuantity() +
               ", 短款投注金额: " + batch.getShortBillBetAmount().setScale(2, HALF_DOWN) +
               ", 短款有效投注金额: " + batch.getShortBillEffBetAmount().setScale(2, HALF_DOWN) +
               ", 短款输赢值: " + batch.getShortBillWlValue().setScale(2, HALF_DOWN) +
               "\n============== 不相同数据 ==============\n" +
               "不相同注单数: " + batch.getAbnormalAmountUnitQuantity() +
               ", 不相同投注金额: " + batch.getAbnormalBetAmount().setScale(2, HALF_DOWN) +
               ", 不相同有效投注金额: " + batch.getAbnormalEffBetAmount().setScale(2, HALF_DOWN) +
               ", 不相同输赢值: " + batch.getAbnormalWlValue().setScale(2, HALF_DOWN);
    }

    /**
     * 构建 Lark 告警卡片内容（对账失败或不平）
     */
    private String buildFailureContents(TReconciliationBatch batch,
                                        TReconciliationBatchRuleRecord ruleRecord,
                                        Throwable throwable) throws JsonProcessingException {
        var contents = new ArrayList<LarkCardDTO.Element>();

        // 判断执行结果和颜色
        boolean unbalanced = Integer.valueOf(3).equals(batch.getBatchStatus())
                             || Integer.valueOf(3).equals(batch.getReviewBatchStatus());
        String resultText = (throwable == null && unbalanced) ? "对账不平" : "对账失败";
        String resultColor = "red"; // 对账失败或不平都红色

        // 基础信息
        contents.add(buildFieldElement("**站点代码:** " + siteProperties.getCode()));
        contents.add(buildFieldElement("**货币单位:** " + siteProperties.getCurrency()));
        contents.add(buildFieldElement("**基准时区:** " + siteProperties.getZoneId()));
        contents.add(buildFieldElement("**厅号:** " + batch.getChannelId()));
        contents.add(buildFieldElement("**厅名:** " + batch.getChannelName()));
        contents.add(buildFieldElement("**对账日期:** <font color=\"red\">" + batch.getBatchDate() + "</font>"));
        contents.add(buildFieldElement("**对账批次号:** " + batch.getBatchNumber()));
        contents.add(buildFieldElement("**执行结果:** <font color=\"" + resultColor + "\">" + resultText + "</font>"));

        if (!"对账不平".equals(resultText)) {
            contents.add(buildFieldElement("**提示信息:** <font color=\"red\">" +
                                           ofNullable(throwable).map(Throwable::getMessage).orElse("NONE") + "</font>"));
        }

        // HR 分隔
        contents.add(new LarkCardDTO.Element().setTag(ELEMENT_TAG_HR));

        // 汇总数据
        contents.add(buildTextElement("📊 汇总数据"));
        contents.add(buildFieldElement("注单数(内部): <font color=\"blue\">" + batch.getInBetQuantity() + "</font>"));
        contents.add(buildFieldElement("注单数(外部): <font color=\"blue\">" + batch.getOutBetQuantity() + "</font>"));
        contents.add(buildFieldElement("投注金额(内部): <font color=\"blue\">" + batch.getInBetAmount().setScale(2, HALF_DOWN) + "</font>"));
        contents.add(buildFieldElement("投注金额(外部): <font color=\"blue\">" + batch.getOutBetAmount().setScale(2, HALF_DOWN) + "</font>"));
        contents.add(buildFieldElement("有效投注金额(内部): <font color=\"blue\">" + batch.getInEffBetAmount().setScale(2, HALF_DOWN) + "</font>"));
        contents.add(buildFieldElement("有效投注金额(外部): <font color=\"blue\">" + batch.getOutEffBetAmount().setScale(2, HALF_DOWN) + "</font>"));
        contents.add(buildFieldElement("输赢值(内部): <font color=\"blue\">" + batch.getInWlValue().setScale(2, HALF_DOWN) + "</font>"));
        contents.add(buildFieldElement("输赢值(外部): <font color=\"blue\">" + batch.getOutWlValue().setScale(2, HALF_DOWN) + "</font>"));

        // HR 分隔
        contents.add(new LarkCardDTO.Element().setTag(ELEMENT_TAG_HR));

        // 平账数据
        contents.add(buildTextElement("✅ 平账数据"));
        contents.add(buildFieldElement("平账注单数: " + batch.getReconBillUnitQuantity()));
        contents.add(buildFieldElement("平账投注金额: " + batch.getReconBetAmount().setScale(2, HALF_DOWN)));
        contents.add(buildFieldElement("平账有效投注金额: " + batch.getReconEffBetAmount().setScale(2, HALF_DOWN)));
        contents.add(buildFieldElement("平账输赢值: " + batch.getReconWlValue().setScale(2, HALF_DOWN)));

        // 长款数据
        contents.add(buildTextElement("⚠ 长款数据"));
        contents.add(buildFieldElement("长款注单数: " + batch.getLongBillUnitQuantity()));
        contents.add(buildFieldElement("长款投注金额: " + batch.getLongBillBetAmount().setScale(2, HALF_DOWN)));
        contents.add(buildFieldElement("长款有效投注金额: " + batch.getLongBillEffBetAmount().setScale(2, HALF_DOWN)));
        contents.add(buildFieldElement("长款输赢值: " + batch.getLongBillWlValue().setScale(2, HALF_DOWN)));

        // 短款数据
        contents.add(buildTextElement("❗ 短款数据"));
        contents.add(buildFieldElement("短款注单数: " + batch.getShortBillUnitQuantity()));
        contents.add(buildFieldElement("短款投注金额: " + batch.getShortBillBetAmount().setScale(2, HALF_DOWN)));
        contents.add(buildFieldElement("短款有效投注金额: " + batch.getShortBillEffBetAmount().setScale(2, HALF_DOWN)));
        contents.add(buildFieldElement("短款输赢值: " + batch.getShortBillWlValue().setScale(2, HALF_DOWN)));

        // 不相同数据
        contents.add(buildTextElement("🔍 不相同数据"));
        contents.add(buildFieldElement("不相同注单数: " + batch.getAbnormalAmountUnitQuantity()));
        contents.add(buildFieldElement("不相同投注金额: " + batch.getAbnormalBetAmount().setScale(2, HALF_DOWN)));
        contents.add(buildFieldElement("不相同有效投注金额: " + batch.getAbnormalEffBetAmount().setScale(2, HALF_DOWN)));
        contents.add(buildFieldElement("不相同输赢值: " + batch.getAbnormalWlValue().setScale(2, HALF_DOWN)));

        // 对账规则信息
        if (ruleRecord != null) {
            contents.add(new LarkCardDTO.Element().setTag(ELEMENT_TAG_HR));
            contents.add(buildTextElement("📘 本次对账规则"));
            contents.add(buildFieldElement("时间单位: " + ruleRecord.getTimeUnitTypes()));
            contents.add(buildFieldElement("是否总分对账: " + getTipsWith(ruleRecord.getHasSummaryReconciliation())));
            contents.add(buildFieldElement("是否对比注单数: " + getTipsWith(ruleRecord.getHasCheckTotalUnitQuantity())));
            contents.add(buildFieldElement("是否对比投注金额: " + getTipsWith(ruleRecord.getHasCheckBetAmount())));
            contents.add(buildFieldElement("是否对比有效投注金额: " + getTipsWith(ruleRecord.getHasCheckEffBetAmount())));
            contents.add(buildFieldElement("是否对比输赢值: " + getTipsWith(ruleRecord.getHasCheckWlValue())));
            contents.add(buildFieldElement("对账时间类型: " + getEnumBy(ruleRecord.getReconciliationDateFieldType()).name()));
        }

        String title = "对账不平".equals(resultText) ? "对账不平警告" : "对账失败警告";
        return objectMapper.writeValueAsString(getLarkCardMessage(title, contents));
    }

    /**
     * 构建带字段的 Element（常用）
     */
    private LarkCardDTO.Element buildFieldElement(String content) {
        return new LarkCardDTO.Element()
                .setTag(ELEMENT_TAG_DIV)
                .setFields(List.of(Map.of(
                        ELEMENT_FIELDS_IS_SHORT, true,
                        ELEMENT_FIELDS_TEXT, Map.of(
                                "tag", ELEMENT_FIELDS_TAG_LARK_MD,
                                ELEMENT_FIELDS_CONTENT, content
                        )
                )));
    }

    /**
     * 构建纯文本 Element（用于标题或说明）
     */
    private LarkCardDTO.Element buildTextElement(String title) {
        return new LarkCardDTO.Element()
                .setTag(ELEMENT_TAG_DIV)
                .setText(Map.of(
                        "tag", ELEMENT_FIELDS_TAG_LARK_MD,
                        ELEMENT_FIELDS_CONTENT, "**" + title + "**"
                ));
    }

    private String getTipsWith(Boolean bool) {
        return ofNullable(bool)
                .map(b -> b ? "是" : "否")
                .orElse("未提供");
    }


    /**
     * 组装卡片模板
     *
     * @param title    消息标题
     * @param contents 消息内容
     * @return LarkCardDTO
     */
    @SneakyThrows
    public LarkCardDTO getLarkCardMessage(String title, List<LarkCardDTO.Element> contents) {

        LarkCardDTO larkCardDTO = new LarkCardDTO();
        // 创建卡片对象
        var card = new LarkCardDTO.Card();
        // 设置正文
        card.setElements(contents);
        // 设置标题及卡片头信息
        card.setHeader(
                // 设置标题
                new LarkCardDTO.Header()
                        .setTitle(new LarkCardDTO.TitleObject().setContent(title))
                        .setTemplate("red")
        );
        larkCardDTO.setCard(card);
        return larkCardDTO;
    }

    /**
     * 发送lark告警信息*
     *
     * @param contents List<String>
     */
    void sendLarkAlert(String contents) {
        try {
//            String requestStr = gson.toJson(convertToLarkMsgFormat(title, contents));
            log.info("LarkAlertBotServiceImpl::sendLarkAlert # contents -> {}", contents);
            String result = HttpUtil.post(larkAlertBotWebhookUrl, contents);
            log.info("LarkAlertBotServiceImpl::sendLarkAlert # result -> {}", result);
        } catch (Exception e) {
            log.error("LarkAlertBotServiceImpl::sendLarkAlert error -> {}", e.getMessage(), e);
        }
    }

    /**
     * 转换为lark富文本发送消息体，结构查看文档 *
     * <a href="https://open.larksuite.com/document/client-docs/bot-v3/add-custom-bot?lang=en-US#c64c7709">
     * https://open.larksuite.com/document/client-docs/bot-v3/add-custom-bot?lang=en-US#c64c7709</a>
     *
     * @param title    String
     * @param contents List<String>
     * @return LarkAlertRequestDTO
     */
    private LarkAlertDTO convertToLarkMsgFormat(String title, List<String> contents) {
        long timestamp = DateUtil.currentSeconds();
        LarkAlertDTO larkAlertDTO = new LarkAlertDTO();
        larkAlertDTO.setMsgType(LARK_ALERT_MSG_TYPE_POST)
                .setTimestamp(String.valueOf(timestamp))
                .setSign(genSign(larkAlertBotSignature, String.valueOf(timestamp)));

        LarkAlertDTO.Content content = new LarkAlertDTO.Content();
        List<List<LarkAlertDTO.Message>> contentMessageList = new ArrayList<>();

        contents.stream().map(msg -> {
            List<LarkAlertDTO.Message> messageList = new ArrayList<>();
            LarkAlertDTO.Message message = new LarkAlertDTO.Message();
            message.setTag("div");
            message.setTag("text").setText(msg);
            messageList.add(message);
            return messageList;
        }).forEach(contentMessageList::add);
        LarkAlertDTO.ZhCn zhCn = new LarkAlertDTO.ZhCn()
                .setTitle(title)
                .setContent(contentMessageList);

        content.setPost(new LarkAlertDTO.Post(zhCn));
        larkAlertDTO.setContent(content);
        return larkAlertDTO;
    }

    /**
     * 生成签名*
     *
     * @param secret    lark机器人密钥
     * @param timestamp 时间戳(单位：秒)
     * @return 签名
     */
    private String genSign(String secret, String timestamp) {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(new byte[]{});
            return new String(Base64.encodeBase64(signData));
        } catch (NoSuchAlgorithmException e) {
            log.error("LarkAlertBotServiceImpl genSign NoSuchAlgorithmException", e);
        } catch (InvalidKeyException e) {
            log.error("LarkAlertBotServiceImpl genSign InvalidKeyException", e);
        }
        return CharSequenceUtil.EMPTY;
    }


    /**
     * @author : Colson
     * @program : c66-sms-api
     * @description : lark告警机器人请求入参
     * @create : 2024-11-07 15:05
     * <p>
     * 具体数据结构如下<br>
     * <pre>
     *  {
     *      "msg_type": "post",
     *      "content": {
     *          "post": {
     *              "zh_cn": {
     *                  "title": "Project Update Notification",
     *                  "content": [
     *                      [
     *                          {
     *                              "tag": "text",
     *                              "text": "Item has been updated: "
     *                          },
     *                          {
     *                              "tag": "a",
     *                              "text": "Please check",
     *                              "href": "http://www.example.com/"
     *                          },
     *                          {
     *                              "tag": "at",
     *                              "user_id": "************"
     *                          }
     *                      ]
     *                  ]
     *              }
     *          }
     *      }
     *  }
     * <pre/>
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class LarkAlertDTO {

        /**
         * 签名*
         */
        private String sign;

        /**
         * 时间戳*
         */
        private String timestamp;

        /**
         * 富文本：post*
         */
        private String msgType;

        /**
         * 富文本内容*
         */
        private Content content;

        @Getter
        @Setter
        @Accessors(chain = true)
        @NoArgsConstructor
        public static class Content {
            private Post post;                      // POST内容
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Post {
            private ZhCn zhCn;                      // 中文内容
        }

        @Getter
        @Setter
        @Accessors(chain = true)
        @NoArgsConstructor
        public static class ZhCn {
            private String title;                   // 标题
            private List<List<Message>> content;    // 内容列表（嵌套列表）
        }

        @Getter
        @Setter
        @Accessors(chain = true)
        public static class Message {
            private String tag;                     // 标签: text, a, at
            private String text;                    // 显示文本
            private String href;                    // 链接(只有 tag = "a" 时有此字段)
            private String userId;                  // 用户ID(只有 tag = "at" 时有此字段)
            private Map<String, String> style;      // 式样

            private Message() {
            }

            public static Message newWithText(String text) {
                return new Message().setTag("text").setText(text);
            }

            public static Message newWithHref(String href) {
                return new Message().setTag("a").setHref(href);
            }

            public static Message newWithUserId(String userId) {
                return new Message().setTag("at").setUserId(userId);
            }

        }
    }


    @Getter
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class LarkCardDTO {
        private String msg_type = "interactive";
        private Card card;

        @Getter
        @Setter
        @Accessors(chain = true)
        @NoArgsConstructor
        public static class Card {
            private Header header;
            private List<Element> elements;
        }


        @Setter
        @Getter
        @Accessors(chain = true)
        @NoArgsConstructor
        public static class Header {
            private TitleObject title;
            private String template = "red";
        }


        @Getter
        @Setter
        @Accessors(chain = true)
        @NoArgsConstructor
        public static class TitleObject {
            private String tag = "plain_text";
            private String content;
        }

        @Setter
        @Getter
        @Accessors(chain = true)
        @NoArgsConstructor
        public static class Element {
            private String tag = "markdown";
            private String content;
            private List<Object> fields;
            private Map<String, Object> text;
        }

    }


}
