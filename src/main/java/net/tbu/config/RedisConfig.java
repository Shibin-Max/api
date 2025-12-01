package net.tbu.config;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
class RedisConfig {
    @Resource
    private Environment environment;
    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.ssl:false}")
    private boolean tlsSwitch;

    @Bean ("redisClusterConfig")
    public RedisClusterConfiguration redisClusterConfig() {
        Map<String, Object> source = new HashMap<>(8);
        source.put("spring.redis.cluster.nodes", environment.getProperty("spring.redis.cluster.nodes"));
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration",
                source));
        redisClusterConfiguration.setPassword(environment.getProperty("spring.redis.password"));
        redisClusterConfiguration.setMaxRedirects(Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.redis.cluster.maxRedirects"))));
        return redisClusterConfiguration;
    }

    @Bean ("lettuceConnectionFactory")
    @Primary
    public LettuceConnectionFactory lettuceConnectionFactory(GenericObjectPoolConfig<Object> redisPool, @Qualifier(
            "redisClusterConfig") RedisClusterConfiguration redisClusterConfig) {
        // 支持自适应集群拓扑刷新和动态刷新源
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enableAllAdaptiveRefreshTriggers()
                // 开启自适应刷新
                .enableAdaptiveRefreshTrigger()
                // 开启定时刷新
                .enablePeriodicRefresh(Duration.ofSeconds(30))
                .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions).build();

        LettuceClientConfiguration clientConfig;
        if(tlsSwitch){
            clientConfig = LettucePoolingClientConfiguration.builder().clientOptions(clusterClientOptions)
                    .commandTimeout(Duration.ofMillis(timeout))
                    .poolConfig(redisPool)
                    .useSsl()
                    .build();
        }else {
            clientConfig = LettucePoolingClientConfiguration.builder().clientOptions(clusterClientOptions)
                    .commandTimeout(Duration.ofMillis(timeout))
                    .poolConfig(redisPool)
                    .build();
        }

        return new LettuceConnectionFactory(redisClusterConfig,clientConfig);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
    public GenericObjectPoolConfig<Object> redisPool() {
        return new GenericObjectPoolConfig<>();
    }


    @Bean("gameRedisTemplate")
    public RedisTemplate<Serializable, Object> redisTemplate(@Qualifier("lettuceConnectionFactory") LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<Serializable, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setDefaultSerializer(new StringRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean("stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("lettuceConnectionFactory") LettuceConnectionFactory giLettuceConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(giLettuceConnectionFactory);
        template.setDefaultSerializer(new StringRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}

