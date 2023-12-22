package org.javaboy.pagoda.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtil {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        static {
                // 添加 Java 8 日期/时间类型模块
                objectMapper.registerModule(new JavaTimeModule());
                // 禁用将日期/时间类型转换为时间戳的功能
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        public static <T> String serialize(T object) {
                try {
                        return objectMapper.writeValueAsString(object);
                } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to serialize object to JSON.", e);
                }
        }

        public static <T> T deserialize(String json, Class<T> targetType) {
                try {
                        return objectMapper.readValue(json, targetType);
                } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to deserialize JSON to object.", e);
                }
        }

        public static <T> T deserialize(String json, TypeReference<T> targetType) {
                try {
                        return objectMapper.readValue(json, targetType);
                } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to deserialize JSON to object.", e);
                }
        }
}
