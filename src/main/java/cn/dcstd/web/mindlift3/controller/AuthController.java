package cn.dcstd.web.mindlift3.controller;


import cn.dcstd.web.mindlift3.common.AuthAccess;
import cn.dcstd.web.mindlift3.common.Result;
import cn.dcstd.web.mindlift3.configuration.GlobalConfiguration;
import cn.dcstd.web.mindlift3.entity.UserDO;
import cn.dcstd.web.mindlift3.mapper.AuthMapper;
import cn.dcstd.web.mindlift3.service.RedisService;
import cn.dcstd.web.mindlift3.service.RegisterByPhoneService;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author NaNo1c
 */
@RestController
@RequestMapping("/auth")
@Transactional(rollbackFor = Exception.class)
public class AuthController {

    @Resource
    private RegisterByPhoneService registerByPhoneService;

    @Resource
    private RedisService redisService;

    @Resource
    private AuthMapper authMapper;

    @Resource
    private GlobalConfiguration globalConfiguration;

    /**
     * 创建API请求
     * @return
     * @throws Exception
     */
    public com.aliyun.teaopenapi.models.Params createApiInfo() throws Exception {
        return new com.aliyun.teaopenapi.models.Params()
                // 接口名称
                .setAction("SendSms")
                // 接口版本
                .setVersion("2017-05-25")
                // 接口协议
                .setProtocol("HTTPS")
                // 接口 HTTP 方法
                .setMethod("POST")
                .setAuthType("AK")
                .setStyle("RPC")
                // 接口 PATH
                .setPathname("/")
                // 接口请求体内容格式
                .setReqBodyType("json")
                // 接口响应体内容格式
                .setBodyType("json");
    }

    /**
     * 创建验证码客户端
     * @return
     * @throws Exception
     */
    public com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(globalConfiguration.getALIBABA_CLOUD_ACCESS_KEY_ID())
                .setAccessKeySecret(globalConfiguration.getALIBABA_CLOUD_ACCESS_KEY_SECRET());
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * 获取验证码
     * @param phone 手机号
     * @return Token
     */
    @AuthAccess
    @GetMapping("/verify-code")
    public Result getVerifyCode(@RequestParam String phone) {
        int verifyCode = registerByPhoneService.getVerifyCode(phone);
        System.out.println("OK > " + verifyCode);

        try {

            com.aliyun.teaopenapi.Client client = createClient();
            System.out.println("client > " + client);
            com.aliyun.teaopenapi.models.Params params = createApiInfo();
            System.out.println("params > " + params);
            // query params
            java.util.Map<String, Object> queries = new java.util.HashMap<>();
            queries.put("PhoneNumbers", phone);
            queries.put("SignName", "智愈星桥");
            queries.put("TemplateCode", "SMS_479935104");
            queries.put("TemplateParam", "{'code':'" + verifyCode + "'}");

            System.out.println(queries);

            // runtime options
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest()
                    .setQuery(com.aliyun.openapiutil.Client.query(queries));
            Map<String, ?> response = client.callApi(params, request, runtime);


            return Result.success("验证码发送成功!", verifyCode);
        } catch (Exception e) {
            return Result.fail("验证码发送失败! > " + e.getMessage());
        }
    }

    /**
     * 手机号注册
     * @param phone 手机号
     * @param code 验证码
     * @return session
     */
    @AuthAccess
    @PostMapping("/register/phone")
    public Result registerByPhone(@RequestParam String phone, @RequestParam String code) {
        // 用户存在性检验
        if(registerByPhoneService.isExistPhone(phone)) {
            return Result.fail("注册失败", "该手机号已被注册!");
        }
        // 验证码有误
        if(!registerByPhoneService.checkVerifyCode(phone, code)) {
            return Result.fail("注册失败", "验证码错误!");
        }
        // 注册成功-写入数据库-签发session(用于修改手机号)
        Random random = new Random();
        String account = "user_" + System.currentTimeMillis() + random.nextInt(1000);
        authMapper.insertUserByPhone(account, phone);
        HashMap map = new HashMap();
        redisService.set(account, phone);
        map.put("session", account);

        //清除Redis缓存
        redisService.del(phone);

        return Result.success(map);
    }

    /**
     * 初次注册设置密码
     * @param session session
     * @param password 密码
     * @return Token
     */
    @AuthAccess
    @PostMapping("/register/phone/password")
    public Result registerByPhoneSetPassword(@RequestParam String session, @RequestParam String password) {
        String phone = (String) redisService.get(session);
        authMapper.updateUserPasswordByPhone(phone, password);
        UserDO userDO = authMapper.selectUserByAccount(session);
        String token = TokenUtil.createToken(userDO.getId(), userDO.getPassword());

        authMapper.insertUserInfoByUid(userDO.getId(), session);

        //清除Redis缓存
        redisService.del(session);

        return Result.success("注册成功!", "注册成功!", token);
    }

    /**
     * 手机号登录
     * @param phone 手机号
     * @param code 验证码
     * @return Token
     */
    @AuthAccess
    @PostMapping("/login/phone")
    public Result loginByPhone(@RequestParam String phone, @RequestParam String code) {
        if(!registerByPhoneService.isExistPhone(phone)){
            Result res = registerByPhone(phone, code);
            return Result.pause(res.getData());
        }
        if(!registerByPhoneService.checkVerifyCode(phone, code)) {
            return Result.fail("登录失败", "验证码错误!");
        }
        UserDO userDO = authMapper.getUserByPhone(phone);
        String token = TokenUtil.createToken(userDO.getId(), userDO.getPassword());

        redisService.del(phone);

        return Result.success("登录成功!", "登录成功!", token);
    }

    /**
     * 账号密码注册
     * @param account 账号
     * @param password 密码
     * @return
     */
    @AuthAccess
    @PostMapping("/register/account")
    public Result registerByAccount(@RequestParam String account, @RequestParam String password) {
        if(registerByPhoneService.isExistAccount(account)) {
            return Result.fail("注册失败", "该手机号已被注册!");
        }
        authMapper.insertUserByAccount(account, password);
        UserDO userDO = authMapper.selectUserByAccount(account);
        String token = TokenUtil.createToken(userDO.getId(), userDO.getPassword());
        authMapper.insertUserInfoByUid(userDO.getId(), account);

        return Result.success("注册成功!", "注册成功!", token);

    }

    /**
     * 账号密码登录
     * @param account 账号
     * @param password 密码
     * @return
     */
    @AuthAccess
    @PostMapping("/login/account")
    public Result loginByAccount(@RequestParam String account, @RequestParam String password) {
        UserDO userDO = authMapper.selectUserByAccount(account);
        if(userDO == null) {
            return Result.fail("登录失败", "账号不存在!");
        }
        if(!userDO.getPassword().equals(password)) {
            return Result.fail("登录失败", "账号或密码错误!");
        }
        String token = TokenUtil.createToken(userDO.getId(), userDO.getPassword());
        return Result.success("登录成功!", "登录成功!", token);
    }


}
