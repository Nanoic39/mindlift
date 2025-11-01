package cn.dcstd.web.mindlift3.entity;

import lombok.Data;
import java.util.Map;

/**
 * @author NaNo1c
 */
@Data
public class JudgeSubmitDTO {
    private int judgeId;
    private int historyId;
    private Map<Integer, Integer> answers; // key: questionId, value: answer value
}

