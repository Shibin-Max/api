package net.tbu.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.config.SslProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedissonConfig {
    @Resource
    private RedisClusterConfigProperties redisClusterConfigProperties;

    //添加redisson的bean
    @Bean
    public Redisson redisson() {
        //redisson版本是3.5，集群的ip前面要加上“redis://”，不然会报错，3.2版本可不加
        List<String> clusterNodes = new ArrayList<>();
        String redisProtocol = Boolean.TRUE.equals(redisClusterConfigProperties.getSsl()) ? "rediss://" : "redis://";
        for (int i = 0; i < redisClusterConfigProperties.getCluster().getNodes().size(); i++) {
            clusterNodes.add(redisProtocol + redisClusterConfigProperties.getCluster().getNodes().get(i));
        }
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress(clusterNodes.toArray(new String[0]))
                .setPassword(redisClusterConfigProperties.getPassword());
        if (Boolean.TRUE.equals(redisClusterConfigProperties.getSsl())) {
            config.useClusterServers()
                    .setSslEnableEndpointIdentification(true)
                    .setSslProvider(SslProvider.JDK);
        }

        return (Redisson) Redisson.create(config);
    }
}
