package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author NaNo1c
 */
@Data
public class UserInfoBaseInfo {
    private String gender;
    private Timestamp birthday;
    private String profession;
    private String relationship;
    private String taste;
    private String color;
}
