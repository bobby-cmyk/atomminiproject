package vttp.miniproject.atomnotes.configurations;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class AppConfig {
    private final Logger logger = Logger.getLogger(AppConfig.class.getName());

    // Get all the redis configuration into the class
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.username}")
    private String redisUsername;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Value("${spring.data.redis.database1}")
    private int redisDatabase1;

    @Value("${spring.data.redis.database2}")
    private int redisDatabase2;

    @Value("${spring.data.redis.database3}")
    private int redisDatabase3;

    @Bean("redisTemplate1")
    public RedisTemplate<String, String> createRedisTemplate1() {
        return createRedisTemplate(redisDatabase1);
    }

    @Bean("redisTemplate2")
    public RedisTemplate<String, String> createRedisTemplate2() {
        return createRedisTemplate(redisDatabase2);
    }

    @Bean("redisTemplate3")
    public RedisTemplate<String, String> createRedisTemplate3() {
        return createRedisTemplate(redisDatabase3);
    }
    
    // Give it a name that we can call
    // Could also create a constant
    private RedisTemplate<String, String> createRedisTemplate(int database) {
        // Create a database configuration
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        config.setDatabase(database);

        // Set the username and password if provided
        if (!redisUsername.trim().isEmpty()) {
            logger.info(">>>> Setting Redis username and password");
            config.setUsername(redisUsername);
            config.setPassword(redisPassword);
        }

        // Create a connection to the database
        JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();

        // Create a factory to connect to Redis
        JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet();

        // Create the RedisTemplate
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisFac);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}