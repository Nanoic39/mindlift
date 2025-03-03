package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DriftBottleReceivedVO {
    private int id;
    private int driftBottleId;
    private String nickName;
    private String content;
    private String oriContent;
    private Timestamp createTime;

}
