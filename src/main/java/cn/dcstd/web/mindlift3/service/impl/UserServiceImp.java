package cn.dcstd.web.mindlift3.service.impl;

import cn.dcstd.web.mindlift3.entity.UserInfoBaseInfo;
import cn.dcstd.web.mindlift3.entity.UserInfoDO;
import cn.dcstd.web.mindlift3.entity.UserInfoVO;
import cn.dcstd.web.mindlift3.mapper.UserMapper;
import cn.dcstd.web.mindlift3.service.UserService;
import cn.dcstd.web.mindlift3.utils.FileUtil;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author NaNo1c
 */
@Service
public class UserServiceImp implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private FileUtil fileUtil;

    @Override
    public void editAvatar(int avatarId) {
        userMapper.editAvatar(TokenUtil.getCurrentUser().getId(), avatarId);
    }

    @Override
    public UserInfoVO getUserInfo() {
        UserInfoVO userInfoVO = new UserInfoVO();

        UserInfoDO userInfoDO = userMapper.getUserInfo(TokenUtil.getCurrentUser().getId());

        userInfoVO.setUserId(userInfoDO.getUserId());
        String avatar;
        try {
            avatar = fileUtil.getFilePath(userInfoDO.getAvatar());
        } catch (Exception ignored) {
            avatar = null;
        }
        userInfoVO.setAvatar(avatar);

        userInfoVO.setNickname(userInfoDO.getNickname());
        userInfoVO.setIntro(userInfoDO.getIntro());

        userInfoVO.setEmotionRating((JSONObject) userInfoDO.getEmotionRating());
        userInfoVO.setGlobalMessageProgress(userInfoDO.getGlobalMessageProgress());
        UserInfoBaseInfo baseInfo = new UserInfoBaseInfo();
        String baseInfoStr = userInfoDO.getBaseInfo().toString();

        // 解析字符串并设置 UserInfoBaseInfo 对象的属性
        parseBaseInfo(baseInfoStr, baseInfo);
        userInfoVO.setBaseInfo(baseInfo);

        return userInfoVO;
    }

    private void parseBaseInfo(String baseInfoStr, UserInfoBaseInfo baseInfo) {
        Pattern pattern = Pattern.compile("(\\w+)=(.+?)(?=,|$)");
        Matcher matcher = pattern.matcher(baseInfoStr);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);

            switch (key) {
                case "gender":
                    baseInfo.setGender(value);
                    break;
                case "birthday":
                    baseInfo.setBirthday(Timestamp.valueOf(value));

                    break;
                case "profession":
                    baseInfo.setProfession(value);
                    break;
                case "relationship":
                    baseInfo.setRelationship(value);
                    break;
                case "taste":
                    baseInfo.setTaste(value);
                    break;
                case "color":
                    baseInfo.setColor(value);
                    break;
                default:
                    // 处理未知的键
                    break;
            }
        }
    }

    @Override
    public void editUserInfo(UserInfoVO userInfoVO) {
        System.out.println(userInfoVO.getBaseInfo());
        userMapper.editUserInfo(userInfoVO.getUserId(), String.valueOf(userInfoVO.getBaseInfo()), userInfoVO.getNickname(), userInfoVO.getIntro(), userInfoVO.getGlobalMessageProgress());
    }
}
