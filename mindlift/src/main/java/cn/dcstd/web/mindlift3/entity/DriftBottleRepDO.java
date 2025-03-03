package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author NaNo1c
 */
@Data
public class DriftBottleRepDO {
    private int id;
    private int driftBottleId;
    private int userId;
    private String content;
    private Timestamp createTime;
}
