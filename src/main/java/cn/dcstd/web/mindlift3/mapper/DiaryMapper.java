package cn.dcstd.web.mindlift3.mapper;

import cn.dcstd.web.mindlift3.entity.DiaryHistoryDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author NaNo1c
 */
@Mapper
public interface DiaryMapper {

    @Select("SELECT * FROM diary_history WHERE userId = #{userId} ORDER BY id DESC")
    List<DiaryHistoryDO> getMyDiaryList(int userId);

    @Insert("INSERT INTO diary_history(userId, todayStatus, title, content, createTime, updateTime, status) VALUES (#{userId}, #{todayStatus}, #{title}, #{content}, NOW(), NOW(), 1)")
    void addDiary(int userId, String todayStatus, String title, String content);

    @Select("SELECT * FROM diary_history WHERE userId = #{id} ORDER BY id desc LIMIT 1")
    DiaryHistoryDO getLastDiary(@Param("id") int id);
}
