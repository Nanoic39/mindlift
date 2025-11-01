package cn.dcstd.web.mindlift3.entity;

import lombok.Data;
import java.util.Map;

/**
 * @author NaNo1c
 */
@Data
public class JudgeResultVO {
    private int id;
    private int judgeId;
    private String judgeTitle;
    private Integer totalScore;
    private Map<String, DimensionScore> dimensionScores;
    private String analysis;
    private String createTime;

    private String totalJudgeResult; // 总的评价结果
    private String judgeResultSuggest; // 测评结果建议


    
    @Data
    public static class DimensionScore {
        private int score;
        private int maxScore;
        private String level; // 低、中、高
        private String description;
    }
}

