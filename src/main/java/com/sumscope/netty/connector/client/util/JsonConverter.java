package com.sumscope.netty.connector.client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Json format converter.
 *
 * @date 2020-05-18
 */
public class JsonConverter {

    /**
     * LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * MAPPER.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    private JsonConverter() {
    }

    /**
     * 转换成字符串或者返回为Null。
     *
     * @param target 目标对象。
     * @param <T>    泛型。
     * @return JSON字符串。
     */
    public static <T> String toStringOrNull(T target) {
        if (null == target) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            LOGGER.error("Json converter to string failed!", e);
            return null;
        }
    }

    /**
     * JSON字符串转换为对象或返回Null。
     *
     * @param json  JSON.
     * @param clazz 转换目标类。
     * @param <T>   泛型。
     * @return 对象。
     */
    public static <T> T parseOrNull(String json, Class<T> clazz) {
        if ("".equals(json) || null == json) {
            return null;
        }

        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            LOGGER.error("Json converter parse to object failed!", e);
            return null;
        }
    }

    /**
     * JSON转换为自定义泛型类或返回Null。
     *
     * @param json      JSON。
     * @param valueType 类型参考。
     * @param <T>       泛型。
     * @return 自定义泛型类。
     */
    public static <T> T parseReferenceOrNull(String json, TypeReference<T> valueType) {
        if ("".equals(json) || null == json) {
            return null;
        }

        try {
            return MAPPER.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            LOGGER.error("Json converter parse reference to object failed!", e);
            return null;
        }
    }
}
