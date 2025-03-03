package cn.dcstd.web.mindlift3.service;

import cn.dcstd.web.mindlift3.entity.UserInfoDO;
import cn.dcstd.web.mindlift3.entity.UserInfoVO;
import cn.dcstd.web.mindlift3.utils.TokenUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author NaNo1c
 */
@Service
public interface UserService {

    void editAvatar(int avatarId);

    UserInfoVO getUserInfo();

    void editUserInfo(UserInfoVO userInfoVO);
}
