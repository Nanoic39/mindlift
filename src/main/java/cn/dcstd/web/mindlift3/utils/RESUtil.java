package cn.dcstd.web.mindlift3.utils;

import cn.dcstd.web.mindlift3.service.RedisService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author NaNo1c
 */
@Component
public class RESUtil {

    @Resource
    private RedisService redisService;

    public String getRESResponse(String content) {
        try {
            System.out.println("RES");
            System.out.println(content);
            System.out.println(redisService.get(content));
        } catch (Exception e) {
            System.out.println("E");
            System.out.println(e);
        }


        return ((redisService.get(content) != null) ? redisService.get(content).toString() : null);
    }


}
