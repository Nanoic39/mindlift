package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class JudgeDetailVO {
    private int id;
    // 标题
    private String title;
    // 测评介绍
    private String content;
    // 封面图片
    private String cover;
    // 测评次数
    private int count;
    // 测评须知
    private String rule;
    // 参考文献
    private String src;
    // 测评题目数量
    private int num;
}
