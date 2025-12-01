package net.tbu.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
@Data
public class PlatformHttpConfig {

    @Value("${jili.seamless.lineConfig:null}")
    private String JILISeamlessLineConfig;

    @Value("${jiliSP.seamless.lineConfig:null}")
    private String JILISPSeamlessLineConfig;

    @Value("${sl.seamless.lineConfig:null}")
    private String SLSeamlessLineConfig;

    @Value("${pp.seamless.lineConfig:null}")
    private String PPSeamlessLineConfig;

    @Value("${hbn.seamless.lineConfig:null}")
    private String HBNSeamlessLineConfig;

    @Value("${ps.seamless.lineConfig:null}")
    private String PSSeamlessLineConfig;

    @Value("${jdb.seamless.lineConfig:null}")
    private String JDBSeamlessLineConfig;

    @Value("${osm.seamless.lineConfig:null}")
    private String OSMSeamlessLineConfig;
}
