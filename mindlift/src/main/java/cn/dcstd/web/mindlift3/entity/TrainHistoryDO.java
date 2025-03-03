package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TrainHistoryDO {
    private int id;
    private int userId;
    private int trainId;
    private int nextId;
    private Timestamp createTime;
}
