package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class UserDO {
    private int id;
    private String account;
    private String password;
    private String phone;
    private int status;
}
