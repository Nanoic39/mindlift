package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class JudgeVO {
    private int id;
    // 标题
    private String title;
    // 外部显示简介
    private String intro;
    // 封面图片
    private String cover;
    // 测评次数
    private int count;
}
