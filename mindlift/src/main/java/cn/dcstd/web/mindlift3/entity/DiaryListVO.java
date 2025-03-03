package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author NaNo1c
 */
@Data
public class DiaryListVO {
    private int id;
    private String todayStatus;
    private String title;
    private String content;
    private Timestamp createTime;
    private Timestamp updateTime;
}
