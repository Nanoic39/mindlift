package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class JudgeHistoryDO {
    private int id;
    private int userId;
    private int judgeId;
    private String progress;
}
