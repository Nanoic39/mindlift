package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.configuration.GlobalConfiguration;
import cn.dcstd.web.mindlift3.entity.AIStarAO;
import cn.dcstd.web.mindlift3.service.AIService;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIServiceImp implements AIService {
    @Resource
    private GlobalConfiguration globalConfiguration;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public ResponseEntity getAnswer(AIStarAO aiStarAO, String param) {
        Map<String, Object> requestBody = getStringObjectMap(aiStarAO, globalConfiguration, param);

        System.out.println(requestBody);
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 构造请求实体
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 构造URL（带查询参数）
        String url = "https://agentapi.baidu.com/assistant/getAnswer?appId=" + globalConfiguration.getStarAppId() + "&secretKey=" + globalConfiguration.getStarSecretKey();

        // 发送POST请求
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );
        return response;
    }

    private static Map<String, Object> getStringObjectMap(AIStarAO aiStarAO, GlobalConfiguration globalConfiguration, String param) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> value = new HashMap<>();

        value.put("showText", param + aiStarAO.getContent());
        content.put("type", "text");
        content.put("value", value);
        message.put("content", content);
        requestBody.put("message", message);
        requestBody.put("source", globalConfiguration.getStarAppId());
        requestBody.put("from", globalConfiguration.getStarFrom());
        requestBody.put("openId", aiStarAO.getOpenId());
        return requestBody;
    }
}
