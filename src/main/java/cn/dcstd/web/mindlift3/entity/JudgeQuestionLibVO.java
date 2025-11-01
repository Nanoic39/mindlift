package cn.dcstd.web.mindlift3.entity;

import lombok.Data;

@Data
public class JudgeQuestionLibVO {
    private int id;
    private int mainId;
    private String question;
    private String dimension;
    private String options;
    private int historyId;
}
