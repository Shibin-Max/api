package net.tbu.config;

import com.digiplus.common.cipher.provider.EncryptProvider;
import com.zaxxer.hikari.HikariConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsIgnoreCase;
import static com.intech.dbcryptor.Cryptor.decryptContent;
import static net.tbu.common.constants.ComConstant.B55_SITE_CODE;
import static net.tbu.common.constants.ComConstant.C66_SITE_CODE;

@Slf4j
@Configuration
public class DataSourceConfig implements InitializingBean {

    @Resource
    private HikariConfig hikariConfig;

    @Resource
    private SiteProperties siteProperties;

    @Autowired(required = false)
    private EncryptProvider encryptProvider;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Current site code is [{}]", siteProperties.getCode());
        if (equalsIgnoreCase(siteProperties.getCode(), C66_SITE_CODE)) {
            if (hikariConfig != null) {
                try {
                    hikariConfig.setUsername(decryptContent(hikariConfig.getUsername()));
                    hikariConfig.setPassword(decryptContent(hikariConfig.getPassword()));
                    log.info("C66 database user&password decrypt successful");
                } catch (Exception e) {
                    log.error("解密数据库信息错误, 解密前账号: [{}], 密码: [{}]", hikariConfig.getUsername(), hikariConfig.getPassword(), e);
                    throw e;
                }
            }
        } else if (equalsIgnoreCase(siteProperties.getCode(), B55_SITE_CODE)) {
            if (hikariConfig != null) {
                try {
                    hikariConfig.setJdbcUrl(encryptProvider.getConfigValue("spring.datasource.url"));
                    hikariConfig.setUsername(encryptProvider.getConfigValue("spring.datasource.username"));
                    hikariConfig.setPassword(encryptProvider.getConfigValue("spring.datasource.password"));
                    log.info("B55 database user&password decrypt successful");
                } catch (Exception e) {
                    log.error("解密数据库信息错误, 解密前账号: [{}], 密码: [{}]", hikariConfig.getUsername(), hikariConfig.getPassword(), e);
                    throw e;
                }
            }
        } else {
            throw new UnsupportedOperationException("Site:[" + siteProperties.getCode() + "] is not supported");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(decryptContent("hnD:Da{tenJkqakuIPh0QeUAk6qF,6pu1guzVb@ZEbFJ"));
        System.out.println(decryptContent("qsJB5M:2f(`Yy(A:Wu@;smFQwSQeiBE(IRNPfVWlSBJJ"));
    }
}
