package org.javaboy.pagoda.framework.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 * redis配置
 *
 * @author pagoda
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
        @Bean
        @SuppressWarnings(value = {"unchecked", "rawtypes"})
        public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
                RedisTemplate<Object, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);

                JacksonRedisSerializer<Object> serializer = new JacksonRedisSerializer<>(Object.class);

                // 使用StringRedisSerializer来序列化和反序列化redis的key值
                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(serializer);

                // Hash的key也采用StringRedisSerializer的序列化方式
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setHashValueSerializer(serializer);

                template.afterPropertiesSet();
                return template;

        }

      /*   @Bean
        @SuppressWarnings(value = {"unchecked", "rawtypes"})
        public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
                RedisTemplate<Object, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);

                // 配置ObjectMapper以启用类型信息
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.WRAPPER_ARRAY);

                // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化redis的value值
                GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

                // 使用StringRedisSerializer来序列化和反序列化redis的key值
                template.setKeySerializer(new StringRedisSerializer());

                template.setValueSerializer(serializer);

                // Hash的key也采用StringRedisSerializer的序列化方式
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setHashValueSerializer(serializer);

                template.afterPropertiesSet();
                return template;
        } */

        @Bean
        public RedissonClient redisClient() {
                Config config = new Config();

                config.useSingleServer().setAddress("redis://101.42.19.113:6379").setPassword("juhaoru1");

                return Redisson.create(config);
        }

        @Bean
        public DefaultRedisScript<Long> limitScript() {
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                redisScript.setScriptText(limitScriptText());
                redisScript.setResultType(Long.class);
                return redisScript;
        }


        /**
         * 限流脚本
         */
        private String limitScriptText() {
                return "local key = KEYS[1]\n" +
                        "local count = tonumber(ARGV[1])\n" +
                        "local time = tonumber(ARGV[2])\n" +
                        "local current = redis.call('get', key);\n" +
                        "if current and tonumber(current) > count then\n" +
                        "    return tonumber(current);\n" +
                        "end\n" +
                        "current = redis.call('incr', key)\n" +
                        "if tonumber(current) == 1 then\n" +
                        "    redis.call('expire', key, time)\n" +
                        "end\n" +
                        "return tonumber(current);";
        }

        @Bean
        public DefaultRedisScript<Long> checkAndDecreaseStockScript() {
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                redisScript.setScriptText(checkAndDecreaseStockScriptText());
                redisScript.setResultType(Long.class);
                return redisScript;
        }

        private String checkAndDecreaseStockScriptText() {
                return "local key = KEYS[1]\n" +
                        "local orderQuantity = tonumber(ARGV[1])\n" +
                        "local availableStockStr = redis.call('get', key)\n" +
                        "local availableStock = tonumber(availableStockStr)\n" +
                        "if availableStock == nil then\n" +
                        "    return -1\n" +
                        "end\n" +
                        "if orderQuantity > availableStock then\n" +
                        "    return -2\n" +
                        "else\n" +
                        "    local newStock = availableStock - orderQuantity\n" +
                        "    redis.call('set', key, tostring(newStock))\n" +
                        "    return 0\n" +
                        "end";
        }

        @Bean
        public DefaultRedisScript<Long> addOrderToStreamScript() {
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                redisScript.setScriptText(addOrderToStreamScriptText());
                redisScript.setResultType(Long.class);
                return redisScript;
        }

        private String addOrderToStreamScriptText() {
                return "redis.call('xadd', 'stream.orders', '*', 'orderId', ARGV[1], 'storeCode', ARGV[2], 'userId', ARGV[3],'orderQuantity', ARGV[4], 'cartItems', ARGV[5])";
        }

}
