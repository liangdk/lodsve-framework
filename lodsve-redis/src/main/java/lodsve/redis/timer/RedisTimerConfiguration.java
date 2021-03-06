package lodsve.redis.timer;

import lodsve.redis.core.annotations.EnableRedis;
import lodsve.redis.core.connection.LodsveRedisConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis计时器配置.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 16/1/12 下午9:23
 */
@Configuration
@ComponentScan("lodsve.redis.timer")
@EnableRedis(name = "redisTimer")
public class RedisTimerConfiguration {
    @Autowired
    private RedisTimerListener listener;
    @Autowired
    @Qualifier("redisTimer")
    private LodsveRedisConnectionFactory connectionFactory;

    @Bean
    public RedisTemplate<String, Object> redisTimerRedisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisEventRedisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listener, new PatternTopic("__keyevent@*:expired"));

        return container;
    }
}
