package cn.dcstd.web.mindlift3.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


/**
 * @author NaNo1c
 */
@Data
public class UserInfoDO {
    private int userId;
    // 基本信息（性别，生日，职业，关系，口味，颜色）
    private String baseInfo;
    // 敏感信息（姓名）
    private String sensitiveInfo;
    private int avatar;
    private String nickname;
    private String intro;
    private JSON emotionRating;
    private String globalMessageProgress;
}
