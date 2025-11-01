package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class VideoListVO {
    private String videoFileName;
    private String videoFilePath;
    private String senderName;
    private String senderAvatar;
    private int likedCount;
    private String coverFilePath;

}
