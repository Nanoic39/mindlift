package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class FileLibDO {
    private int id;
    private String fileType;
    private String fileName;
    private String fileExtension;
    private int uploaderId;
}
