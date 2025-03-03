package cn.dcstd.web.mindlift3.mapper;

import cn.dcstd.web.mindlift3.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author NaNo1c
 */
@Mapper
public interface JudgeMapper {

    @Select("SELECT * FROM judge_lib")
    List<JudgeLibDO> getJudgeList();

    @Select("SELECT * FROM judge_lib WHERE id = #{id}")
    JudgeLibDO getJudgeDetail(@Param("id") int id);

    @Select("SELECT count(*) FROM judge_question_lib WHERE mainId = #{id}")
    int getJudgeQuestionNumById(@Param("id") int id);

    @Select("SELECT * FROM judge_question_lib WHERE mainId = #{id}")
    List<JudgeQuestionLibDO> getJudgeQuestion(@Param("id") int id);

    @Update("UPDATE judge_lib SET count = count + 1 WHERE id = #{id}")
    void addCount(int id);

    @Insert("INSERT INTO judge_history (userId, judgeId) VALUES (#{userId}, #{judgeId})")
    void addHistory(@Param("userId") int userId, @Param("judgeId") int judgeId);

    @Select("SELECT id FROM judge_history WHERE userId = #{id}")
    int getJudgeHistoryIdByUid(@Param("id") int id);

    @Update("UPDATE judge_history SET progress = #{progress} WHERE id = #{hid}")
    void updateHistory(int hid, String progress);

    @Select("SELECT * FROM judge_history WHERE userId = #{id}")
    List<JudgeHistoryDO> getJudgeHistory(@Param("id") int id);
}
