package cn.dcstd.web.mindlift3.mapper;

import cn.dcstd.web.mindlift3.entity.UserInfoDO;
import com.alibaba.fastjson.JSON;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author NaNo1c
 */
@Mapper
public interface UserMapper {

    @Update("UPDATE user_info SET avatar = #{avatarId} WHERE userId = #{id}")
    void editAvatar(int id, int avatarId);

    @Select("SELECT * FROM user_info WHERE userId = #{id}")
    UserInfoDO getUserInfo(int id);

    @Update("UPDATE user_info SET baseInfo = #{baseInfo}, nickname = #{nickname}, intro = #{intro}, globalMessageProgress = #{globalMessageProgress} WHERE userId = #{userId}")
    void editUserInfo(int userId, String baseInfo, String nickname, String intro, String globalMessageProgress);
}
