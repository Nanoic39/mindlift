package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author NaNo1c
 */
@Data
public class DriftBottleLibDO {
    private int id;
    private int userId;
    private String content;
    private String tags;
    private Timestamp createTime;
}
