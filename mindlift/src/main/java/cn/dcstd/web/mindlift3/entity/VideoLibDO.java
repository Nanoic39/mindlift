package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class VideoLibDO {
    private int id;
    private int videoFileId;
    private int CoverFileId;
    private String title;
    private String intro;
    private int senderId;
    private int status;
    private int typeId;
}
