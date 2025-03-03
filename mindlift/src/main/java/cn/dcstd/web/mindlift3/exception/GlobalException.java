package cn.dcstd.web.mindlift3.exception;

import lombok.Getter;

/**
 * @author NaNo1c
 */

//GlobalException.java
@Getter
public enum GlobalException {
    //枚举
    EMPTY(300, "返回结果为空","查不到数据捏 >.<"),
    ERROR_NOT_LOGIN(401, "身份验证失败(未登录)", "为什么不登录就能来这里捏（思）"),
    ERROR_TOKEN(402, "身份验证失败(token有误)", "你的token和你的账户不匹配诶！"),
    ERROR_AUTH(403, "权限不足", "前面的道路...取得权限后再来探索喵~"),
    ERROR_NOT_FOUND(404, "未找到目标资源", "诶，我辣么大一个文件去哪里呐?O.O"),
    ERROR_REGISTER(600100, "注册失败", "用户名或密码有误！"),
    ERROR_REPEAT_REGISTER(600101, "注册失败", "该账号已存在!若注册账号的非你本人请及时联系官方账号申诉，防止信息泄露！"),
    ERROR_WARNING_REGISTER(600102, "注册失败", "哒咩渗透!该行为已上报,多次进行该行为可能导致您的设备被封禁"),
    ERROR_LOGIN(600200, "登录失败", "ohno!您可能还没有注册..."),
    BAN_LOGIN(600201, "登录失败", "该账号已被封禁，如有疑问请联系管理员/官方账号"),
    BAN_SAY(600300, "发言失败", "该账号已被禁言，如有疑问请联系管理员/官方账号"),
    ERROR_ACTIVE_APPLICANT_NUM(800001, "申请失败", "已到达最大同时活动申请上限，请等待其它申请结束");

    private int code;
    private String msg;
    private String tips;

    GlobalException(int code, String msg, String tips) {
        this.code = code;
        this.msg = msg;
        this.tips = tips;
    }
}
