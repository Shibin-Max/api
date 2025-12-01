package net.tbu.spi.strategy.channel.dto.facai;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.tbu.common.enums.PlatformEnum;
import net.tbu.spi.strategy.channel.dto.LobbyBasicReq;
import org.springframework.http.HttpMethod;

/**
 * 注单feign请求参数
 */
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"uri", "platformId", "httpMethod", "connectTimeout", "readTimeout"})
@Data
@Accessors(chain = true)
public class FacaiLobbyOrderReq extends LobbyBasicReq {

    private String StartDate;
    private String EndDate;

    public static void main(String[] args) {
        FacaiLobbyOrderReq req = new FacaiLobbyOrderReq();
        req.setPlatformId(PlatformEnum.FACAI.getPlatformId());
        req.setUri("/GetHistoryRecordList");
        req.setStartDate("2025-04-15 00:00:00");
        req.setEndDate("2025-04-15 23:59:59");
        req.setHttpMethod(HttpMethod.POST.name());
        System.out.println(JSON.toJSONString(req));

    }

}