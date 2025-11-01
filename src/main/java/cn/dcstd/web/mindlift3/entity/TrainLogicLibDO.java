package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

/**
 * @author NaNo1c
 */
@Data
public class TrainLogicLibDO {
    private int id;
    private int mainId;
    private String content;
    private String options;
    private int isFirst;
    private int historyId;
}
