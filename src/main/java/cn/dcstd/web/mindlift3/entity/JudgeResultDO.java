package cn.dcstd.web.mindlift3.entity;

import lombok.Data;
import java.sql.Timestamp;

/**
 * @author NaNo1c
 */
@Data
public class JudgeResultDO {
    private int id;
    private int userId;
    private int judgeId;
    private Integer totalScore;
    private String dimensionScores; // JSON格式
    private String analysis; // 分析报告
    private Timestamp createTime;
}

