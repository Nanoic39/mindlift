package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author NaNo1c
 */
@Data
public class DriftBottleVO {
    private int id;
    private int senderId;
    private String content;
    private String tags;
    private Timestamp createTime;
}
