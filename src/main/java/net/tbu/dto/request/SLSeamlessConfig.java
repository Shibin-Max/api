package net.tbu.dto.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * SL厅nacos配置请求厅方参数
 * </p>
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SLSeamlessConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * lobbyUrl nacos配置
     */
    private String lobbyUrl;

    /**
     * agentId nacos配置
     */
    private String agentId;

    /**
     * agentKey nacos配置
     */
    private String agentKey;

    /**
     * SL 日汇总注单接口路径，nacos配置*
     */
    private String dailyOrdersUrlPath;

    /**
     * SL 查询玩家注单接口路径, nacos配置*
     */
    private String detailOrdersUrlPath;

//    /**
//     * 游戏厅对应的房间号 nacos配置
//     */
//    private List<Game> channelVidsConfig;
//
//    /**
//     * 开始时间, 必填
//     */
//    private String beginTime;
//
//    /**
//     * 结束时间, 必填
//     */
//    private String endTime;
//
//    /**
//     * 当前进厅游戏, 详⻅枚举 *
//     */
//    private String sourceGame;
//
//    /**
//     * 游戏类型
//     */
//    private String gametype;
//
//    /**
//     * 房间id列表, 枚举参考4.2.1*
//     */
//    private List<String> vidList;
//
//    @Data
//    public static class Game {
//        private String name;
//        private String platformId;
//        private String sourceGame;
//        private String gametype;
//        private List<String> vidList;
//    }

    public static void main(String[] args) {
        String json = """
                {
                	"agentId": "eC69Wallet",
                	"agentKey": "agentPwdReal",
                	"lobbyUrl": "https://gateway.g22-prod.com",
                	"dailyOrdersUrlPath": "/getDailyOrders",
                	"detailOrdersUrlPath": "/getOrders",
                	"channelVidsConfig": [{
                		"name": "AQUA",
                		"platformId": "183",
                		"sourceGame": "G09",
                		"gametype": "IRGO",
                		"vidList": ["IR01", "IR02"]
                	}, {
                		"name": "BINGO",
                		"platformId": "079",
                		"sourceGame": "G04",
                		"gametype": "",
                		"vidList": ["BG01", "FB01"]
                	}, {
                		"name": "COLORGAME",
                		"platformId": "172",
                		"sourceGame": "G13",
                		"gametype": "",
                		"vidList": ["CG01", "CW01"]
                	}, {
                		"name": "EBG",
                		"platformId": "080",
                		"sourceGame": "G09",
                		"gametype": "EBGO",
                		"vidList": ["EB01", "EB02", "EB03"]
                	}, {
                		"name": "GINTO",
                		"platformId": "178",
                		"sourceGame": "G09",
                		"gametype": "MBGO",
                		"vidList": ["MB01", "MB02", "MB03"]
                	}, {
                		"name": "PDB",
                		"platformId": "186",
                		"sourceGame": "G20",
                		"gametype": "RBDG",
                		"vidList": ["RB01"]
                	}, {
                		"name": "PULAPUTI",
                		"platformId": "184",
                		"sourceGame": "G16",
                		"gametype": "RBGO",
                		"vidList": ["RG01"]
                	}]
                }""";

        SLSeamlessConfig dto = JSONObject.parseObject(json, SLSeamlessConfig.class);

        System.out.println(dto);

    }

}
