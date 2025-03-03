package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

@Data
public class JudgeHistoryVO {
    private int id;
    private int judgeId;
    private String progress;
    // 标题
    private String title;
    // 外部显示简介
    private String intro;
    // 封面图片
    private String cover;
    // 测评次数
    private int num;
}
