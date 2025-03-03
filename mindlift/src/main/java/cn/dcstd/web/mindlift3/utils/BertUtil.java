package cn.dcstd.web.mindlift3.utils;

import cn.dcstd.web.mindlift3.configuration.GlobalConfiguration;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component
public class BertUtil {

    @Resource
    private GlobalConfiguration globalConfiguration;

    @Resource
    private RestTemplate restTemplate;

    public String getBertResponse(String content) throws IOException {
        HttpURLConnection connection = getHttpURLConnection(content);

        int responseCode = connection.getResponseCode();
        //System.out.println("POST Response Code :: " + responseCode);

        //success
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return switch (JSON.parseObject(String.valueOf(response)).get("predicted_emotion").toString()) {
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

    private HttpURLConnection getHttpURLConnection(String content) throws IOException {
        URL url = new URL(globalConfiguration.getBertApi());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Apifox/1.0.0 (https://apifox.com)");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Host", "127.0.0.1:8000");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setDoOutput(true);

        String jsonInputString = "{\"text\": \"" + content + "\"}";

        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        return connection;
    }

}
