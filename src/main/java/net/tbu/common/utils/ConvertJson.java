package net.tbu.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;

public class ConvertJson {

    public static String convertJson(@NonNull JSONObject json) {
        return json.toJSONString();
    }

    public static String convertJson(@NonNull String json) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(json);
    }

}
