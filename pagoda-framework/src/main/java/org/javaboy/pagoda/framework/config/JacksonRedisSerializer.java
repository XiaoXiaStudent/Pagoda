package org.javaboy.pagoda.framework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

public class JacksonRedisSerializer<T> implements RedisSerializer<T> {

        private Class<T> clazz;
        private ObjectMapper objectMapper = new ObjectMapper();

        public JacksonRedisSerializer(Class<T> clazz) {
                this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) throws SerializationException {
                if (t == null) {
                        return new byte[0];
                }
                try {
                        return objectMapper.writeValueAsBytes(t);
                } catch (IOException e) {
                        throw new SerializationException(e.getMessage(), e);
                }
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null || bytes.length == 0) {
                        return null;
                }
                try {
                        return objectMapper.readValue(bytes, clazz);
                } catch (IOException e) {
                        throw new SerializationException(e.getMessage(), e);
                }
        }
}
