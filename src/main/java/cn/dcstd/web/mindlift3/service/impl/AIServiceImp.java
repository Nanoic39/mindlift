package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.configuration.GlobalConfiguration;
import cn.dcstd.web.mindlift3.entity.AIStarAO;
import cn.dcstd.web.mindlift3.service.AIService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;
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

    @Override
    public String getFaceStatus(MultipartFile file) throws IOException {
        HttpURLConnection connection = getHttpURLConnection(file);

        int responseCode = connection.getResponseCode();

        //success
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return switch (JSON.parseObject(String.valueOf(response)).get("emotion").toString()) {
                case "happiness" -> "开心";
                case "disgust" -> "厌恶";
                case "anger" -> "愤怒";
                case "like" -> "喜欢";
                case "surprise" -> "惊讶";
                case "fear" -> "恐惧";
                default -> "悲伤";
            };
        } else {
            return "null";
        }
    }

    private HttpURLConnection getHttpURLConnection(MultipartFile image) throws IOException {
        URL url = new URL(globalConfiguration.getResApi());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Apifox/1.0.0 (https://apifox.com)");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=--------------------------942425007512048713555208");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Host", "127.0.0.1:8081");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setDoOutput(true);

        // 写入请求体（包含图片数据）
        try (OutputStream outputStream = connection.getOutputStream()) {
            String boundary = "--------------------------942425007512048713555208";
            StringBuilder requestBody = new StringBuilder();

            // 添加图片部分
            requestBody.append("--").append(boundary).append("\r\n");
            requestBody.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(image.getOriginalFilename()).append("\"\r\n");
            requestBody.append("Content-Type: ").append(image.getContentType()).append("\r\n");
            requestBody.append("\r\n");

            // 写入文本部分到输出流
            outputStream.write(requestBody.toString().getBytes());

            // 写入图片数据到输出流
            outputStream.write(image.getBytes());

            // 结束边界
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
        }

        return connection;
    }


    private static Map<String, Object> getStringObjectMap(AIStarAO aiStarAO, GlobalConfiguration globalConfiguration, String param) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> value = new HashMap<>();

        value.put("showText", aiStarAO.getContent() + param);
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
