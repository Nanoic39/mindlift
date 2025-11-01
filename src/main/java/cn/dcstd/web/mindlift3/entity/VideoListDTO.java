package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class VideoListDTO {
    private int id;
    private int fileId;
    private int typeId;
    private String fileName;
    private String filePath;
    private String senderName;
    private int CoverFileId;
    private int senderId;
}
