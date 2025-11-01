package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

@Data
public class JudgeLibDO {
    private int id;
    // 标题
    private String title;
    // 外部显示简介
    private String intro;
    // 测评介绍
    private String content;
    // 封面图片
    private int coverId;
    // 测评次数
    private int count;
    // 测评须知
    private String rule;
    // 参考文献
    private String src;
}
