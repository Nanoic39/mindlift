package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.entity.UserDO;
import cn.dcstd.web.mindlift3.mapper.AuthMapper;
import cn.dcstd.web.mindlift3.service.RedisService;
import cn.dcstd.web.mindlift3.service.RegisterByPhoneService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author NaNo1c
 */
@Service
public class RegisterByPhoneServiceImp implements RegisterByPhoneService {

    @Resource
    private RedisService redisService;

    @Resource
    private AuthMapper authMapper;

    /**
     * 检测手机号是否存在
     * @param phone 手机号
     * @return -1：不存在，0：存在但未激活，1：已激活
     */
    @Override
    public boolean isExistPhone(String phone) {
        UserDO userDO = authMapper.getUserByPhone(phone);
        System.out.println(userDO);
        if(userDO == null) {
            return false;
        } else {
            // 数据库等于 0：未完成 才等于0，否则状态都是已存在(封禁/存在)
            return userDO.getStatus() != 0;
        }
    }

    @Override
    public int getVerifyCode(String phone) {
        Random random = new Random();
        String characters = "23456789";
        StringBuilder verifyCodeTemp = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            verifyCodeTemp.append(characters.charAt(index));
        }

        //生成验证码
        String verifyCode = verifyCodeTemp.toString();
        //写入缓存
        redisService.set(phone, verifyCodeTemp, 60 * 5);

        return Integer.parseInt(verifyCode);
    }

    @Override
    public boolean checkVerifyCode(String phone, String code) {
        String verifyCode = (String) redisService.get(phone);
        return verifyCode != null && verifyCode.equals(code);
    }
}
