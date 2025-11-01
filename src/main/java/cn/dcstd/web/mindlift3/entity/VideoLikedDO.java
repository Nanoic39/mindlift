package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class VideoLikedDO {
    private int id;
    private int videoId;
    private int likerId;
    private int status;
}
