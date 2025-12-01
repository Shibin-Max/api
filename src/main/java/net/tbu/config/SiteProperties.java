package net.tbu.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import static net.tbu.common.constants.ComConstant.C66_SITE_CODE;

@Data
@ToString
@Component
@RefreshScope
@ConfigurationProperties(prefix = "site")
public class SiteProperties {

    private String code = C66_SITE_CODE;

    private String zoneId = "Asia/Shanghai";

    private String currency = "PHP";

    public String getCode() {
        return code.toUpperCase().trim();
    }

    public String getCurrency() {
        return currency.toUpperCase().trim();
    }

}
