package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author Nano1c
 */
@Data
public class AIStarAO {
    private String threadId;
    private String content;
    // 用户唯一标识(可以在进入页面时生成唯一时间戳)
    private String openId;
}
