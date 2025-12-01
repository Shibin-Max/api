package net.tbu.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
/**
 * 调用JDB厅接口的请求类
 */
public class JdbSeamlessLineDtoReq {

  /**
   * 客户域名
   */
  @JsonProperty("dc")
  private String dc;

  /**
   * 加密
   */
  @JsonProperty("x")
  private String x;


}
