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

    @Select("SELECT id FROM judge_history WHERE userId = #{userId} AND judgeId = #{judgeId} ORDER BY id DESC LIMIT 1")
    Integer getLatestJudgeHistoryId(@Param("userId") int userId, @Param("judgeId") int judgeId);

    @Update("UPDATE judge_history SET progress = #{progress} WHERE id = #{hid}")
    void updateHistory(int hid, String progress);

    @Select("SELECT * FROM judge_history WHERE userId = #{id}")
    List<JudgeHistoryDO> getJudgeHistory(@Param("id") int id);

    @Insert("INSERT INTO judge_result (userId, judgeId, totalScore, dimensionScores, analysis) " +
            "VALUES (#{userId}, #{judgeId}, #{totalScore}, #{dimensionScores}, #{analysis})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertJudgeResult(JudgeResultDO judgeResultDO);

    @Update("UPDATE judge_history SET answers = #{answers}, resultId = #{resultId} WHERE id = #{id}")
    void updateJudgeHistoryWithResult(@Param("id") int id, 
                                       @Param("answers") String answers, 
                                       @Param("resultId") int resultId);

    @Select("SELECT * FROM judge_result WHERE id = #{id}")
    JudgeResultDO getJudgeResultById(@Param("id") int id);

    @Select("SELECT jr.* FROM judge_result jr " +
            "JOIN judge_history jh ON jr.id = jh.resultId " +
            "WHERE jh.userId = #{userId} AND jr.judgeId = #{judgeId} " +
            "ORDER BY jr.createTime DESC")
    List<JudgeResultDO> getJudgeResultsByUserAndJudge(@Param("userId") int userId, 
                                                       @Param("judgeId") int judgeId);
}
