package com.isariev.gatewayservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperUtils {

    ObjectMapper objectMapper = new ObjectMapper();

    public <T> T objectMapper(Object object, Class<T> contentClass) {
        return objectMapper.convertValue(object, contentClass);
    }
}
