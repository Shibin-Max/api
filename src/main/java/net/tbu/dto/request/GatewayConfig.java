package net.tbu.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GatewayConfig {
    private String envDomain;
    private String envUri;
    private String dataUri;
    private String login;
    private String password;

}

