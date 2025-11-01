package cn.dcstd.web.mindlift3.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class UserInfoVO {
    private int userId;
    // 基本信息（性别，生日，职业，关系，口味，颜色）
    private UserInfoBaseInfo baseInfo;
    private String avatar;
    private String nickname;
    private String intro;
    private JSONObject emotionRating;
    private String globalMessageProgress;
}
