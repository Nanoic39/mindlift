package cn.dcstd.web.mindlift3.mapper;

import cn.dcstd.web.mindlift3.entity.UserDO;
import cn.dcstd.web.mindlift3.entity.UserInfoDO;
import org.apache.ibatis.annotations.*;

/**
 * @author NaNo1c
 */
@Mapper
public interface AuthMapper {

    @Select("select * from user where phone = #{phone}")
    public UserDO getUserByPhone(@Param("phone") String phone);

    @Insert("insert into user(account, phone, status) values(#{account}, #{phone}, 0)")
    void insertUserByPhone(@Param("account") String account, @Param("phone") String phone);

    @Select("select * from user where id = #{uid}")
    UserDO selectUserById(@Param("uid") String uid);

    @Update("update user set password = #{password}, status = 1 where phone = #{phone}")
    void updateUserPasswordByPhone(@Param("phone") String phone, @Param("password") String password);

    @Select("select * from user where account = #{session}")
    UserDO selectUserByAccount(@Param("session") String session);

    @Insert("insert into user_info(userId, nickname, intro, globalMessageProgress) values(#{id}, #{account}, '这个人暂时没有写简介...', '0')")
    void insertUserInfoByUid(@Param("id") int id, @Param("account") String account);

    @Select("select * from user_info where userId = #{userId}")
    UserInfoDO selectUserInfoByUid(@Param("userId") int userId);
}
