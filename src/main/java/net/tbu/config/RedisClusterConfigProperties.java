package net.tbu.config;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisClusterConfigProperties {
    private String password;
    private Cluster cluster;
    private Boolean ssl = false;

    @Setter
    @Getter
    public static class Cluster {
        private List<String> nodes;
    }

}
