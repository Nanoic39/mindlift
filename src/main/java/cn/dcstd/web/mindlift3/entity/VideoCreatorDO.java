package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class VideoCreatorDO {
    private int id;
    private String name;
    private int avatarId;
    private int userId;
    private String intro;
}
