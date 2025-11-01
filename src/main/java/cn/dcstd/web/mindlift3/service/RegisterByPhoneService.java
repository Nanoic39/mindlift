package cn.dcstd.web.mindlift3.service;

import org.springframework.stereotype.Service;

/**
 * @author NaNo1c
 */
@Service
public interface RegisterByPhoneService {

    /**
     * 判断手机号是否存在
     * @param phone 手机号
     * @return [boolean] true：存在，false：不存在
     */
    boolean isExistPhone(String phone);

    /**
     * 获取验证码
     * @param phone 手机号
     */
    int getVerifyCode(String phone);

    /**
     * 检查验证码
     * @param phone 手机号
     * @param code 验证码
     * @return [boolean] true：验证成功，false：验证失败
     */
    boolean checkVerifyCode(String phone, String code);

    boolean isExistAccount(String account);
}
